package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.FileManagerDto;
import com.bekaku.api.spring.dto.ResponseMessage;
import com.bekaku.api.spring.dto.UploadRequest;
import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.FileMime;
import com.bekaku.api.spring.model.FilesDirectory;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.service.FileMimeService;
import com.bekaku.api.spring.service.FilesDirectoryService;
import com.bekaku.api.spring.service.UserService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.FileUtil;
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
import java.util.HashMap;
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
        return this.responseDeleteMessage();
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

    @DeleteMapping("/internalDeleteFileApi/{id}")
    public ResponseEntity<Object> internalDeleteFileApi(@PathVariable("id") Long id, @AuthenticationPrincipal UserDto auth, HttpServletRequest request) {
        Optional<FileManager> fileManager = fileManagerService.findById(id);
        if (fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        logger.info("internalDeleteFileApi > id:{}", id);
        fileManagerService.deleteFileBy(fileManager.get());
        return responseEntity(new HashMap<String, Object>() {{
            put("id", id);
            put(ConstantData.SERVER_TIMESTAMP, DateUtil.getLocalDateTimeNow());
        }}, HttpStatus.OK);
    }

    //    @PreAuthorize("isHasPermission('file_manager_manage')")
    @PostMapping("/uploadApi")
    public ResponseEntity<Object> uploadApi(@RequestParam(ConstantData.FILES_UPLOAD_ATT) MultipartFile file,
                                            @RequestParam(name = "fileDirectoryId", required = false, defaultValue = "0") long fileDirectoryId,
                                            @RequestParam(name = "resizeImage", required = false, defaultValue = "1") boolean resizeImage,
                                            @AuthenticationPrincipal UserDto user) {

        if (file.isEmpty()) {
            throw this.responseError(HttpStatus.OK, null, i18n.getMessage("error.fileUploadNotFound"));
        }

        FileManager f = uploadProcess(file, user, resizeImage);
        if (f == null) {
            throw this.responseErrorBadRequest();
        }
        Optional<FilesDirectory> directory = Optional.empty();
        if (fileDirectoryId > 0) {
            directory = filesDirectoryService.findById(fileDirectoryId);
        }
        directory.ifPresent(f::setFilesDirectory);
        fileManagerService.save(f);
        return this.responseEntity(fileManagerService.convertEntityToDto(f), HttpStatus.OK);

//        String mimeType = FileUtil.getMimeType(file).toLowerCase();
//        boolean isImage = FileUtil.isImage(mimeType);
//        long fileSize = FileUtil.getFileSize(file);
//        String originalName = FileUtil.getMultipartFileName(file);
//        Optional<String> extenstion = FileUtil.getExtensionByStringHandling(originalName);
//        if (extenstion.isEmpty()) {
//            originalName = FileUtil.generateFileNameByMimeType(user.getId() + "", mimeType);
//        }
//        String finalOriginalName = originalName;
//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("isImage", isImage);
//            put("mimeType", mimeType);
//            put("size", fileSize);
//            put("originalName", finalOriginalName);
//        }}, HttpStatus.OK);
    }

    @PostMapping("/uploadBase64Api")
    public ResponseEntity<Object> uploadBase64Api(@Valid @RequestBody UploadRequest dto, @AuthenticationPrincipal UserDto user) {
        if (AppUtil.isEmpty(dto.getFileBase64())) {
            throw this.responseErrorBadRequest();
        }
        FileManager f = uploadBase64Process(dto, user);
        if (f == null) {
            throw this.responseErrorBadRequest();
        }

        Optional<FilesDirectory> directory = Optional.empty();
        if (dto.getFileDirectoryId() != null && dto.getFileDirectoryId() > 0) {
            directory = filesDirectoryService.findById(dto.getFileDirectoryId());
        }
        directory.ifPresent(f::setFilesDirectory);
        fileManagerService.save(f);
        return this.responseEntity(fileManagerService.convertEntityToDto(f), HttpStatus.OK);


//        String mimeType = FileUtil.detectBase64MimeType(dto.getFileBase64());
//        int size = FileUtil.getFileSizeFromBase64(dto.getFileBase64());
//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("getFileExtensionByMimeType", FileUtil.getFileExtensionByMimeType(mimeType));
//            put("mimeType", mimeType);
//            put("size", size);
//        }}, HttpStatus.OK);
    }

    private void validateAllowMemeType(String mimeType) {
        if (AppUtil.isEmpty(mimeType) || !appProperties.getAllowMimes().contains(mimeType)) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.fileMimeNotAllow"));
        }
    }

    private FileManager uploadBase64Process(UploadRequest dto, UserDto user) {
        String mimeType = FileUtil.detectBase64MimeType(dto.getFileBase64());
        this.validateAllowMemeType(mimeType);
        long fileSize = FileUtil.getFileSizeFromBase64(dto.getFileBase64());
        String originalName = "image_" + System.currentTimeMillis() + ".jpg";
        String yearMonthFolder = FileUtil.getImagesYearMonthDirectory();
        String uploadPath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), yearMonthFolder);
        String newName = FileUtil.generateJpgFileNameByMemeType(user.getId() + "", mimeType);
        byte[] decodedImg = FileUtil.convertBase64ToByteArray(dto.getFileBase64());
        return uploadAndResizeProcess(decodedImg, mimeType, yearMonthFolder, uploadPath, originalName, newName, fileSize, true, dto.isResizeImage());
    }

    private FileManager uploadAndResizeProcess(byte[] bytes, String mimeType, String yearMonthFolder, String uploadPath,
                                               String originalName, String newName, long fileSize, boolean isImage, boolean isResizeImage) {
        if (bytes == null) {
            return null;
        }
        try {
            Path path = Paths.get(uploadPath + newName);
            Files.write(path, bytes);
        } catch (IOException e) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
        //resize image
        if (isImage) {
            if (isResizeImage) {
                thumbnailatorResize(uploadPath, newName);
            }
            //create thumbnail
            if (appProperties.getUploadImage().isCreateThumbnail()) {
                thumbnailatorCreateThumnail(uploadPath, newName);
            }
        }
        Optional<FileMime> fileMime = fileMimeService.findByName(mimeType);
        FileMime fileMimeForSave = fileMime.orElseGet(() -> fileMimeService.save(new FileMime(mimeType)));
        return new FileManager(null, originalName, fileSize, fileMimeForSave, yearMonthFolder + newName);
    }

    private String generateOriginalFileName(MultipartFile file, UserDto user, String mimeType) {
        String originalName = FileUtil.getMultipartFileName(file);
        Optional<String> extension = FileUtil.getExtensionByStringHandling(originalName);
        if (extension.isEmpty()) {
            originalName = FileUtil.generateFileNameByMimeType(user.getId() + "", mimeType);
        }

        return originalName;
    }

    private FileManager uploadProcess(MultipartFile file, UserDto user, boolean resizeImage) {

        String mimeType = FileUtil.getMimeType(file).toLowerCase();
        this.validateAllowMemeType(mimeType);

        boolean isImage = FileUtil.isImage(mimeType);
        long fileSize = FileUtil.getFileSize(file);
        String originalName = generateOriginalFileName(file, user, mimeType);

        String yearMonthFolder = isImage ? FileUtil.getImagesYearMonthDirectory() : FileUtil.getFilesYearMonthDirectory();
        String uploadPath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), yearMonthFolder);
        String newName = isImage ? FileUtil.generateJpgFileName(user.getId() + "", originalName) :
                FileUtil.generateFileName(user.getId() + "", originalName);
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }

        return uploadAndResizeProcess(bytes, mimeType, yearMonthFolder, uploadPath, originalName, newName, fileSize, isImage, resizeImage);
    }


    private void thumbnailatorResize(String uploadFile, String fileName) {

        if (FileUtil.fileExists(uploadFile + fileName)) {
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
    }

    private void thumbnailatorCreateThumnail(String uploadFile, String fileName) {
        if (FileUtil.fileExists(uploadFile + fileName)) {
            try {
                String thumbName = FileUtil.generateThumbnailName(fileName, appProperties.getUploadImage().getThumbnailExname());
                BufferedImage originalImage = ImageIO.read(new File(uploadFile + fileName));
                BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getThumbnailWidth(), appProperties.getUploadImage().getThumbnailWidth(), 1);
                ImageIO.write(outputImage, "jpg", new File(uploadFile + thumbName));
            } catch (IOException e) {
                throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
            }
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
