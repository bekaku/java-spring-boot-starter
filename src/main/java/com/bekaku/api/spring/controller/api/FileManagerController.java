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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RequestMapping(path = "/api/fileManager")
@RestController
@RequiredArgsConstructor
public class FileManagerController extends BaseApiController {

    private final FileManagerService fileManagerService;
    private final FilesDirectoryService filesDirectoryService;
    private final FileMimeService fileMimeService;
    private final I18n i18n;
    private final AppProperties appProperties;
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
        log.info("internalDeleteFileApi > id:{}", id);
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
        String filePath = uploadFile + fileName;
        if (FileUtil.fileExists(filePath)) {
            try {
                int limitWidth = appProperties.getUploadImage().getLimitWidth();
                int limitHeight = appProperties.getUploadImage().getLimitHeight();

                File inputFile = new File(filePath);
                BufferedImage originalImage = ImageIO.read(inputFile);
                if (originalImage != null) {
                    originalImage = FileUtil.correctOrientation(originalImage, inputFile);

                    //get width and height of image
                    int imageWidth = originalImage.getWidth();
                    int imageHeight = originalImage.getHeight();
                    if (imageWidth > limitWidth || imageHeight > limitHeight) {
                        BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getLimitWidth(), appProperties.getUploadImage().getLimitHeight(), 1);
                        ImageIO.write(outputImage, "jpg", new File(filePath));
                    }
                }
            } catch (IOException e) {
                throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
            }
        }
    }

    private void thumbnailatorCreateThumnail(String uploadFile, String fileName) {
        String filePath = uploadFile + fileName;
        if (FileUtil.fileExists(filePath)) {
            try {
                String thumbName = FileUtil.generateThumbnailName(fileName, appProperties.getUploadImage().getThumbnailExname());
                String fileThumnailPath = uploadFile + thumbName;

                File inputFile = new File(filePath);
                BufferedImage originalImage = ImageIO.read(inputFile);
                if (originalImage != null) {
                    originalImage = FileUtil.correctOrientation(originalImage, inputFile);

                    BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getThumbnailWidth(), appProperties.getUploadImage().getThumbnailWidth(), 1);
                    ImageIO.write(outputImage, "jpg", new File(fileThumnailPath));
                }

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

    @GetMapping("/images")
    public ResponseEntity<Resource> getImage(@RequestParam("path") String filename) {
        try {
            Path filePath = Paths.get(appProperties.getUploadPath()).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //TODO best solution
    @GetMapping("/files/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("path") String fileName) {
        /*
        // Construct the file object
        File file = new File(appProperties.getUploadPath(), fileName);

        // Check if the file exists and is within the allowed directory
        try {
            // Get the canonical path of the file and the base directory
            String canonicalFilePath = file.getCanonicalPath();
            String canonicalDirPath = new File(appProperties.getUploadPath()).getCanonicalPath();

            // Ensure the file is within the allowed directory
            if (!canonicalFilePath.startsWith(canonicalDirPath)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Check if the file exists
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            // Log the error or handle it as needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Create an InputStream from the file
        FileInputStream fileInputStream = new FileInputStream(file);

        // Determine the content type based on the file extension
        String mimeType = null;
        try {
            mimeType = FileUtil.getMimeType(file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        log.info("name:{}, mimeType:{}", file.getName(), mimeType);
        // Validate the MIME type
        try {
            validateAllowMemeType(mimeType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }
        // Set headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        // Return the file as a ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType(mimeType))
                .body(new InputStreamResource(fileInputStream));

        */
        try {
            // Validate file path before any operations
            if (!isValidFilePath(fileName)) {
                return ResponseEntity.badRequest().build();
            }
            File file = new File(appProperties.getUploadPath(), fileName);

            // Validate file location and existence
            if (!isFileAccessAllowed(file)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            // Get and validate MIME type
            String mimeType = FileUtil.getMimeType(file);
            if (!isAllowedMimeType(mimeType)) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }
            // Set headers for the response
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
            HttpHeaders headers = prepareHeaders(file);
            // Create an InputStream from the file
            FileInputStream fileInputStream = new FileInputStream(file);
            // Return the file as a ResponseEntity
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(new InputStreamResource(fileInputStream));

        } catch (IOException e) {
            log.error("Error processing file request: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error while processing file request: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //TODO not working as aspect
    @GetMapping("/files/stream")
    public ResponseEntity<StreamingResponseBody> streamFile(@RequestParam("path") String fileName) {
        try {
            // Validate file path before any operations
            if (!isValidFilePath(fileName)) {
                return ResponseEntity.badRequest().build();
            }

            File file = new File(appProperties.getUploadPath(), fileName);

            // Validate file location and existence
            if (!isFileAccessAllowed(file)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            // Get and validate MIME type
            String mimeType = FileUtil.getMimeType(file);
            if (!isAllowedMimeType(mimeType)) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }

            // Prepare response headers
            HttpHeaders headers = prepareHeaders(file);

            // Create streaming response
            StreamingResponseBody responseBody = outputStream -> {
                try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[8192]; // Larger buffer for better performance
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        outputStream.flush(); // Ensure data is written
                    }
                }
            };

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(responseBody);

        } catch (IOException e) {
            log.error("Error processing file request: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error while processing file request: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods to improve code organization and readability
    private boolean isValidFilePath(String fileName) {
        return fileName != null && !fileName.isEmpty() && !fileName.contains("..");
    }

    private boolean isFileAccessAllowed(File file) throws IOException {
        String canonicalFilePath = file.getCanonicalPath();
        String canonicalDirPath = new File(appProperties.getUploadPath()).getCanonicalPath();
        return canonicalFilePath.startsWith(canonicalDirPath);
    }

    private boolean isAllowedMimeType(String mimeType) {
        try {
            validateAllowMemeType(mimeType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private HttpHeaders prepareHeaders(File file) {
        HttpHeaders headers = new HttpHeaders();
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName);
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        return headers;
    }
}
