package com.grandats.api.givedeefive.controller.api;

import com.grandats.api.givedeefive.configuration.I18n;
import com.grandats.api.givedeefive.dto.FileManagerDto;
import com.grandats.api.givedeefive.dto.ResponseMessage;
import com.grandats.api.givedeefive.dto.UserDto;
import com.grandats.api.givedeefive.model.FileManager;
import com.grandats.api.givedeefive.model.FileMime;
import com.grandats.api.givedeefive.model.FilesDirectory;
import com.grandats.api.givedeefive.model.User;
import com.grandats.api.givedeefive.properties.AppProperties;
import com.grandats.api.givedeefive.service.FileManagerService;
import com.grandats.api.givedeefive.service.FileMimeService;
import com.grandats.api.givedeefive.service.FilesDirectoryService;
import com.grandats.api.givedeefive.service.UserService;
import com.grandats.api.givedeefive.util.ConstantData;
import com.grandats.api.givedeefive.util.FileUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RequestMapping(path = "/api/fileManager")
@RestController
@RequiredArgsConstructor
public class FileManagerController extends BaseApiController {

    private final FileManagerService fileManagerService;
    private final FilesDirectoryService filesDirectoryService;
    private final FileMimeService fileMimeService;
    private final I18n i18n;
    private final AppProperties appProperties;
    Logger logger = LoggerFactory.getLogger(FileManagerController.class);
    @Autowired
    private UserService userService;

    @PreAuthorize("isHasPermission('file_manager_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(
            Pageable pageable,
            @RequestParam(value = "directoryId", defaultValue = "0") long directoryId,
            HttpServletRequest request) {
        // http://localhost:8084/api/fileManager?page=1&size=10&sort=fileName,asc&directoryId=0
//    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
//                                          @RequestParam(value = "size", defaultValue = "20") int size,
//                                          @RequestParam(value = "sort", defaultValue = "") String sort,
//                                          Pageable pageable,
//                                          @RequestParam(value = "directoryId", defaultValue = "0") long directoryId,
//                                          HttpServletRequest request) {

//        return this.responseEntity(fileManagerService.findAllFolderAndFileByParentFolder(new Paging(page, size, sort), directoryId > 0 ? directoryId : null), HttpStatus.OK);
        return this.responseEntity(fileManagerService.findAllFolderAndFileByParentFolder(getPaging(pageable), directoryId > 0 ? directoryId : null), HttpStatus.OK);
//        return this.responseEntity(getPaging(pageable), HttpStatus.OK);

//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("page", page);
//            put("size", size);
//            put("sort", sort);
//        }}, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('file_manager_manage')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody FileManagerDto dto) {
        FileManager fileManager = fileManagerService.convertDtoToEntity(dto);
        fileManagerService.save(fileManager);
        return this.responseEntity(fileManagerService.convertEntityToDto(fileManager), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('file_manager_manage')")
    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody FileManagerDto dto) {
        FileManager fileManager = fileManagerService.convertDtoToEntity(dto);
        Optional<FileManager> oldData = fileManagerService.findById(dto.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        fileManagerService.update(fileManager);
        return this.responseEntity(fileManagerService.convertEntityToDto(fileManager), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('file_manager_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<FileManager> fileManager = fileManagerService.findById(id);
        if (fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
//        return this.responseEntity(fileManager.get(), HttpStatus.OK);
        return this.responseEntity(fileManagerService.convertEntityToDto(fileManager.get()), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('file_manager_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) throws IOException {
        Optional<FileManager> fileManager = fileManagerService.findById(id);
        if (fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        fileManagerService.deleteFileBy(fileManager.get());
        return this.reponseDeleteMessage();
    }

    @DeleteMapping("/deleteFileApi/{id}")
    public ResponseEntity<Object> deleteFileApi(@PathVariable("id") Long id, @AuthenticationPrincipal UserDto auth) {
        Optional<FileManager> fileManager = fileManagerService.findById(id);
        if (fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        userService.requireTheSameUser(auth.getId(), fileManager.get().getCreatedUser());
        fileManagerService.deleteFileBy(fileManager.get());
        return this.responseEntity(HttpStatus.OK);
    }

    //    @PreAuthorize("isHasPermission('file_manager_manage')")
    @PostMapping("/uploadApi")
    public ResponseEntity<Object> uploadApi(@RequestParam(ConstantData.FILES_UPLOAD_ATT) MultipartFile file,
                                            @RequestParam("fileDirectoryId") long fileDirectoryId,
                                            @AuthenticationPrincipal UserDto user) {

        if (file.isEmpty()) {
            throw this.responseError(HttpStatus.OK, null, i18n.getMessage("error.fileUploadNotFound"));
        }

        Optional<FilesDirectory> directory = Optional.empty();
        if (fileDirectoryId > 0) {
            directory = filesDirectoryService.findById(fileDirectoryId);
        }
        FileManager f = uploadProcess(file, user);
        directory.ifPresent(f::setFilesDirectory);
        fileManagerService.save(f);
        return this.responseEntity(fileManagerService.convertEntityToDto(f), HttpStatus.OK);
    }

    private FileManager uploadProcess(MultipartFile file, UserDto user) {

        String mimeType = FileUtil.getMimeType(file).toLowerCase();

        if (!appProperties.getAllowMimes().contains(mimeType)) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.fileMimeNotAllow"));
        }

        boolean isImage = FileUtil.isImage(mimeType);
        Long fileSize = FileUtil.getFileSize(file);
        String originalName = FileUtil.getMultipartFileName(file);

        String yearMonthFolder = isImage ? FileUtil.getImagesYearMonthDirectory() : FileUtil.getFilesYearMonthDirectory();
        String uploadPath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), yearMonthFolder);
//        String newName = FileUtil.generateFileName(user.getId() + "", originalName);
        String newName = isImage ? FileUtil.generateJpgFileName(user.getId() + "", originalName) :
                FileUtil.generateFileName(user.getId() + "", originalName);
        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + newName);
            Files.write(path, bytes);
        } catch (IOException e) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
        if (isImage) {
            //resize image
            thumbnailatorResize(uploadPath, newName);
            //create thumbnail
            if (appProperties.getUploadImage().isCreateThumbnail()) {
                thumbnailatorCreateThumnail(uploadPath, newName);
            }
        }

        Optional<FileMime> fileMime = fileMimeService.findByName(mimeType);
        FileMime fileMimeForSave = fileMime.orElseGet(() -> fileMimeService.save(new FileMime(mimeType)));
        return new FileManager(null, originalName, fileSize, fileMimeForSave, yearMonthFolder + newName);
    }

    private void thumbnailatorResize(String uploadFile, String fileName) {
        try {
            int limitWidth = appProperties.getUploadImage().getLimitWidth();
            int limitHeight = appProperties.getUploadImage().getLimitHeight();
            BufferedImage originalImage = ImageIO.read(new File(uploadFile + fileName));

            //get width and height of image
            int imageWidth = originalImage.getWidth();
            int imageHeight = originalImage.getHeight();
            if (imageWidth > limitWidth || imageHeight > limitHeight) {
                BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getLimitWidth(), appProperties.getUploadImage().getLimitHeight(), 1);
                ImageIO.write(outputImage, "jpg", new File(uploadFile + fileName));
            }

        } catch (IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }

    private void thumbnailatorCreateThumnail(String uploadFile, String fileName) {
        try {
            String thumbName = FileUtil.generateThumbnailName(fileName, appProperties.getUploadImage().getThumbnailExname());
            BufferedImage originalImage = ImageIO.read(new File(uploadFile + fileName));
            BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getThumbnailWidth(), appProperties.getUploadImage().getThumbnailWidth(), 1);
            ImageIO.write(outputImage, "jpg", new File(uploadFile + thumbName));
        } catch (IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Object> findPublicOne(@PathVariable("id") long id) {
        Optional<FileManagerDto> dto = fileManagerService.findForPublicById(id);
        if (dto.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(dto.get(), HttpStatus.OK);
    }

    @PutMapping("/updateUserAvatar")
    public ResponseMessage updateUserAvatar(@AuthenticationPrincipal UserDto userAuthen, @RequestParam("fileManagerId") Long fileManagerId) {

        if (userAuthen == null) {
            return new ResponseMessage(HttpStatus.UNAUTHORIZED, null);
        }

        Optional<User> user = userService.findById(userAuthen.getId());
        Optional<FileManager> fileManager = fileManagerService.findById(fileManagerId);
        if (user.isEmpty() || fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }

        //delete old avatar file
        if (user.get().getAvatarFile() != null) {
            fileManagerService.deleteFileBy(user.get().getAvatarFile());
        }


        user.get().setAvatarFile(fileManager.get());
        userService.update(user.get());
        return new ResponseMessage(HttpStatus.OK, null);
    }

    @PutMapping("/updateUserCover")
    public ResponseMessage updateUserCover(@AuthenticationPrincipal UserDto userAuthen, @RequestParam("fileManagerId") Long fileManagerId) {

        if (userAuthen == null) {
            return new ResponseMessage(HttpStatus.UNAUTHORIZED, null);
        }

        Optional<User> user = userService.findById(userAuthen.getId());
        Optional<FileManager> fileManager = fileManagerService.findById(fileManagerId);
        if (user.isEmpty() || fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        //delete old avatar file
        if (user.get().getCoverFile() != null) {
            fileManagerService.deleteFileBy(user.get().getCoverFile());
        }

        user.get().setCoverFile(fileManager.get());
        userService.update(user.get());
        return new ResponseMessage(HttpStatus.OK, null);
    }
}
