package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.FileManagerDto;
import com.bekaku.api.spring.dto.FileUploadChunkMergeRequestDto;
import com.bekaku.api.spring.dto.FileUploadChunkResponseDto;
import com.bekaku.api.spring.dto.ImageDto;
import com.bekaku.api.spring.dto.ResponseMessage;
import com.bekaku.api.spring.dto.UploadRequest;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.FileMime;
import com.bekaku.api.spring.model.FilesDirectory;
import com.bekaku.api.spring.properties.AppDefaultsProperties;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.service.FileMimeService;
import com.bekaku.api.spring.service.FilesDirectoryService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.FileUtil;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import static com.bekaku.api.spring.util.FileUtil.TEMP_UPLOAD_DIR;

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
    private final AppDefaultsProperties appDefaultsProperties;
    private final AppUserService appUserService;
    private final JwtService jwtService;
    private final List<String> sortProperties = List.of("fileName", "createdDate", "updatedDate", "fileSize", "fileMime");
    private final Executor executor = new ThreadPoolTaskExecutor();
    private String fileName;

    @PreAuthorize("isHasPermission('file_manager_list')")
    @GetMapping
    public List<FileManagerDto> findAll(
            Pageable pageable,
            @RequestParam(value = "directoryId", defaultValue = "0") long directoryId,
            @AuthenticationPrincipal AppUserDto auth) {
        // http://localhost:8084/api/fileManager?page=1&size=10&sort=fileName,asc&directoryId=0
//    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
//                                          @RequestParam(value = "size", defaultValue = "20") int size,
//                                          @RequestParam(value = "sort", defaultValue = "") String sort,
//                                          Pageable pageable,
//                                          @RequestParam(value = "directoryId", defaultValue = "0") long directoryId,
//                                          HttpServletRequest request) {

//        return this.responseEntity(fileManagerService.findAllFolderAndFileByParentFolder(new Paging(page, size, sort), directoryId > 0 ? directoryId : null), HttpStatus.OK);
        return fileManagerService.findAllFolderAndFileByParentFolderAndOwnerId(getPaging(pageable, sortProperties), directoryId > 0 ? directoryId : null, auth.getId());
//        return this.responseEntity(getPaging(pageable), HttpStatus.OK);

//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("page", page);
//            put("size", size);
//            put("sort", sort);
//        }}, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('file_manager_list')")
    @GetMapping("/findAllFolder")
    public List<FileManagerDto> findAllFolder(
            Pageable pageable,
            @RequestParam(value = "directoryId", defaultValue = "0") long directoryId,
            @AuthenticationPrincipal AppUserDto auth) {
        return fileManagerService.findAllFolderByParentFolderAndOwnerId(getPaging(pageable, sortProperties), directoryId > 0 ? directoryId : null, auth.getId());
    }

    @PreAuthorize("isHasPermission('file_manager_list')")
    @GetMapping("/findAllFile")
    public List<FileManagerDto> findAllFile(
            Pageable pageable,
            @RequestParam(value = "directoryId", defaultValue = "0") long directoryId,
            @AuthenticationPrincipal AppUserDto auth) {
        return fileManagerService.findAllFileByParentFolderAndOwnerId(getPaging(pageable, sortProperties), directoryId > 0 ? directoryId : null, auth.getId());
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
    public ResponseEntity<Object> deleteFileApi(@PathVariable("id") Long id, @AuthenticationPrincipal AppUserDto auth) {
        Optional<FileManager> fileManager = fileManagerService.findById(id);
        if (fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        appUserService.requireTheSameUser(auth.getId(), fileManager.get().getCreatedUser());
        fileManagerService.deleteFileBy(fileManager.get());
        return this.responseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/internalDeleteFileApi/{id}")
    public ResponseEntity<Object> internalDeleteFileApi(@PathVariable("id") Long id, @AuthenticationPrincipal AppUserDto auth, HttpServletRequest request) {
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

    @PostMapping("/uploadChunkApi")
    public FileUploadChunkResponseDto uploadChunkApi(@RequestParam(ConstantData.FILES_UPLOAD_ATT) MultipartFile file,
                                                     @RequestParam("chunkNumber") int chunkNumber,
                                                     @RequestParam("totalChunks") int totalChunks,
                                                     @RequestParam("originalFilename") String originalFilename,
                                                     @RequestParam("chunkFilename") String chunkFilename,
                                                     @AuthenticationPrincipal AppUserDto user) {

        try {
            String uploadPathTmp = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), TEMP_UPLOAD_DIR);
            String mimeType = FileUtil.getMimeType(file).toLowerCase();
            log.info("mimeType: {}", mimeType);
            if (chunkNumber == 1) {
                this.validateAllowMemeType(mimeType);
            } else {
                if (AppUtil.isEmpty(chunkFilename)) {
                    throw this.responseError(HttpStatus.BAD_REQUEST, null, "File name not found");
                }
                String fileLatestPath = uploadPathTmp + chunkFilename + ".part" + (chunkNumber - 1);
                if (!FileUtil.fileExists(fileLatestPath)) {
                    throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, "File part " + (chunkNumber - 1) + " not found");
                }
            }

//            long fileSize = FileUtil.getFileSize(file);
            String originalName = generateOriginalFileName(file, user, mimeType, originalFilename);
            String fileName;
            FileUploadChunkResponseDto dto = new FileUploadChunkResponseDto();
            if (AppUtil.isEmpty(chunkFilename)) {
                fileName = FileUtil.generateFileName(user.getId() + "", originalName);
                dto.setFilename(fileName);
                dto.setFileMime(mimeType);
            } else {
                fileName = chunkFilename;
            }

            Path uploadDir = Paths.get(uploadPathTmp);
            Path tempFile = uploadDir.resolve(fileName + ".part" + chunkNumber);
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            dto.setLastChunk(chunkNumber == totalChunks);
            dto.setStatus(true);
            return dto;
        } catch (IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, "Failed to upload chunk: " + chunkNumber + " of " + totalChunks + " for " + originalFilename);
        }
    }

    @PostMapping("/mergeChunkApi")
    public FileManagerDto mergeChunkApi(@Valid @RequestBody FileUploadChunkMergeRequestDto dto,
                                        @AuthenticationPrincipal AppUserDto user) {
        log.info("mergeChunkApi > totalChunks:{}, fileMime:{}, originalFilename:{}, chunkFilename:{}, fileDirectoryId:{}",
                dto.getTotalChunks(), dto.getFileMime(), dto.getOriginalFilename(), dto.getChunkFilename(), dto.getFileDirectoryId());
        if (AppUtil.isEmpty(dto.getChunkFilename()) || dto.getTotalChunks() == 0) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, "Missing required parameters.");
        }

        AppUser appUser = appUserService.findAndValidateAppUserBy(user);
        try {
            // Get directory paths
            Path tempFileDir = Paths.get(FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), TEMP_UPLOAD_DIR));

            // Merge file into a temporary merged file first
            Path mergedTempFilePath = tempFileDir.resolve(dto.getChunkFilename() + ".merged");

            try (OutputStream out = Files.newOutputStream(mergedTempFilePath, StandardOpenOption.CREATE)) {
                for (int i = 1; i <= dto.getTotalChunks(); i++) {
                    Path chunkFile = tempFileDir.resolve(dto.getChunkFilename() + ".part" + i);
                    if (!Files.exists(chunkFile)) {
                        throw this.responseError(HttpStatus.NOT_FOUND, null, "Chunk file not found: " + chunkFile.getFileName());
                    }
                    Files.copy(chunkFile, out);
                    Files.delete(chunkFile); // Cleanup after copying
                }
            }

            long fileSize = FileUtil.getFileSize(mergedTempFilePath);
            String mimeType = FileUtil.getMimeType(mergedTempFilePath);
            if (AppUtil.isEmpty(mimeType)) {
                throw this.responseError(HttpStatus.BAD_REQUEST, null, "Missing required mime type.");
            }
            boolean isImage = FileUtil.isImage(mimeType);
            log.info("mergeChunkApi > isImage:{}, mimeType:{}, fileSize:{}", isImage, mimeType, fileSize);
            String yearMonthFolder = isImage ? FileUtil.getImagesYearMonthDirectory() : FileUtil.getFilesYearMonthDirectory();
            String uploadPath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), yearMonthFolder);
            Path finalUploadDir = Paths.get(uploadPath);

            Path finalFilePath = finalUploadDir.resolve(dto.getChunkFilename());
            // Move merged file to final upload folder
            Files.move(mergedTempFilePath, finalFilePath, StandardCopyOption.REPLACE_EXISTING);

            //resize image
            if (isImage) {
                ImageDto imgInfo = getImageInfo(uploadPath + dto.getChunkFilename());
                if (dto.isResizeImage() && canResize(imgInfo)) {
                    thumbnailatorResize(uploadPath, dto.getChunkFilename());
                }
                //create thumbnail
                if (appProperties.getUploadImage().isCreateThumbnail() && canCreateThumnail(imgInfo)) {
                    thumbnailatorCreateThumnail(uploadPath, dto.getChunkFilename());
                }
            }

            Optional<FileMime> mime = fileMimeService.findByName(mimeType.toLowerCase());
            FileMime fileMimeForSave = mime.orElseGet(() -> fileMimeService.save(new FileMime(mimeType.toLowerCase())));
            FileManager f = new FileManager(null, dto.getOriginalFilename(), fileSize, fileMimeForSave, yearMonthFolder + dto.getChunkFilename());
            // Create and return FileManagerDto
            Optional<FilesDirectory> directory = Optional.empty();
            if (dto.getFileDirectoryId() != null && dto.getFileDirectoryId() > 0) {
                directory = filesDirectoryService.findById(dto.getFileDirectoryId());
            }
            directory.ifPresent(f::setFilesDirectory);
            f.setOwner(appUser);
            fileManagerService.save(f);
            return fileManagerService.convertEntityToDto(f);

        } catch (IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, "Failed to merge file: " + dto.getOriginalFilename());
        }
    }

    private ImageDto getImageInfo(String path) {
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {
            log.error("File not found: {}", path);
            return null;
        }

        // File size
        try {
            long fileSize = Files.size(file.toPath());
            long width = -1, height = -1;

            try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                if (!readers.hasNext()) {
                    log.error("No suitable ImageReader found for file: {}", path);
                    return null;
                }

                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis, true, true);
                    width = reader.getWidth(0);
                    height = reader.getHeight(0);
                } finally {
                    reader.dispose();
                }
            }
            log.info("File Info:  {}", String.format("Size: %d bytes, Width: %d px, Height: %d px", fileSize, width, height));
            return new ImageDto(fileSize, width, height);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    //    @PreAuthorize("isHasPermission('file_manager_manage')")
    @PostMapping("/uploadApi")
    public FileManagerDto uploadApi(@RequestParam(ConstantData.FILES_UPLOAD_ATT) MultipartFile file,
                                    @RequestParam(name = "fileDirectoryId", required = false, defaultValue = "0") long fileDirectoryId,
                                    @RequestParam(name = "resizeImage", required = false, defaultValue = "1") boolean resizeImage,
                                    @AuthenticationPrincipal AppUserDto user) {

        if (file.isEmpty()) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.fileUploadNotFound"));
        }

        FileManager f = uploadProcess(file, user, resizeImage);
        if (f == null) {
            throw this.responseErrorBadRequest();
        }
        Optional<FilesDirectory> directory = Optional.empty();
        if (fileDirectoryId > 0) {
            directory = filesDirectoryService.findById(fileDirectoryId);
        }
        AppUser appUser = appUserService.findAndValidateAppUserBy(user);
        f.setOwner(appUser);
        directory.ifPresent(f::setFilesDirectory);
        fileManagerService.save(f);
        return fileManagerService.convertEntityToDto(f);

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
    public ResponseEntity<Object> uploadBase64Api(@Valid @RequestBody UploadRequest dto, @AuthenticationPrincipal AppUserDto user) {
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
        AppUser appUser = appUserService.findAndValidateAppUserBy(user);
        f.setOwner(appUser);
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

    private FileManager uploadBase64Process(UploadRequest dto, AppUserDto user) {
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
            ImageDto imgInfo = getImageInfo(uploadPath + newName);
            if (isResizeImage && canResize(imgInfo)) {
                thumbnailatorResize(uploadPath, newName);
            }
            //create thumbnail
            if (appProperties.getUploadImage().isCreateThumbnail() && canCreateThumnail(imgInfo)) {
                thumbnailatorCreateThumnail(uploadPath, newName);
            }
        }
        Optional<FileMime> fileMime = fileMimeService.findByName(mimeType);
        FileMime fileMimeForSave = fileMime.orElseGet(() -> fileMimeService.save(new FileMime(mimeType)));
        return new FileManager(null, originalName, fileSize, fileMimeForSave, yearMonthFolder + newName);
    }

    private String generateOriginalFileName(MultipartFile file, AppUserDto user, String mimeType, String originalNamePost) {
        String originalName = originalNamePost;
        if (AppUtil.isEmpty(originalName)) {
            originalName = FileUtil.getMultipartFileName(file);
        }
        Optional<String> extension = FileUtil.getExtensionByStringHandling(originalName);
        if (extension.isEmpty()) {
            originalName = FileUtil.generateFileNameByMimeType(user.getId() + "", mimeType);
        }

        return FileUtil.trimFileName(originalName, 125);
    }

    private FileManager uploadProcess(MultipartFile file, AppUserDto user, boolean resizeImage) {

        String mimeType = FileUtil.getMimeType(file).toLowerCase();
        this.validateAllowMemeType(mimeType);

        boolean isImage = FileUtil.isImage(mimeType);
        long fileSize = FileUtil.getFileSize(file);
        String originalName = generateOriginalFileName(file, user, mimeType, null);

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
                File inputFile = new File(filePath);
                BufferedImage originalImage = ImageIO.read(inputFile);
                if (originalImage != null) {
                    originalImage = FileUtil.correctOrientation(originalImage, inputFile);

                    //get width and height of image
                    if (canResize(originalImage)) {
                        BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getLimitWidth(), appProperties.getUploadImage().getLimitHeight(), 1);
                        ImageIO.write(outputImage, "jpg", new File(filePath));
                    }
                }
            } catch (IOException e) {
                throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
            }
        }
    }

    private boolean canResize(BufferedImage originalImage) {
        int imageWidth = originalImage.getWidth();
        int imageHeight = originalImage.getHeight();
        int limitWidth = appProperties.getUploadImage().getLimitWidth();
        int limitHeight = appProperties.getUploadImage().getLimitHeight();
        int maxResolution = appProperties.getUploadImage().getMaxResolution();
        return (imageWidth > limitWidth || imageHeight > limitHeight) && (imageWidth <= maxResolution && imageHeight <= maxResolution);
    }

    private boolean canResize(ImageDto imageDto) {
        if (imageDto == null) {
            return false;
        }
        long imageWidth = imageDto.getWidth();
        long imageHeight = imageDto.getHeight();
        long limitWidth = appProperties.getUploadImage().getLimitWidth();
        long limitHeight = appProperties.getUploadImage().getLimitHeight();
        long maxResolution = appProperties.getUploadImage().getMaxResolution();
        return (imageWidth > limitWidth || imageHeight > limitHeight) && (imageWidth <= maxResolution && imageHeight <= maxResolution);
    }

    private boolean canCreateThumnail(BufferedImage originalImage) {
        int imageWidth = originalImage.getWidth();
        int imageHeight = originalImage.getHeight();
        int maxResolution = appProperties.getUploadImage().getMaxResolution();
        return (imageWidth <= maxResolution && imageHeight <= maxResolution);
    }

    private boolean canCreateThumnail(ImageDto imageDto) {
        if (imageDto == null) {
            return false;
        }
        long imageWidth = imageDto.getWidth();
        long imageHeight = imageDto.getHeight();
        long maxResolution = appProperties.getUploadImage().getMaxResolution();
        return (imageWidth <= maxResolution && imageHeight <= maxResolution);
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
                    if (canCreateThumnail(originalImage)) {
                        originalImage = FileUtil.correctOrientation(originalImage, inputFile);
                        BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getThumbnailWidth(), appProperties.getUploadImage().getThumbnailWidth(), 1);
                        ImageIO.write(outputImage, "jpg", new File(fileThumnailPath));
                    }
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
    public ResponseMessage updateUserAvatar(@AuthenticationPrincipal AppUserDto userAuthen, @RequestParam("fileManagerId") Long fileManagerId) {

        if (userAuthen == null) {
            return new ResponseMessage(HttpStatus.UNAUTHORIZED, null);
        }

        Optional<AppUser> user = appUserService.findById(userAuthen.getId());
        Optional<FileManager> fileManager = fileManagerService.findById(fileManagerId);
        if (user.isEmpty() || fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }

        //delete old avatar file
        if (user.get().getAvatarFile() != null) {
            fileManagerService.deleteFileBy(user.get().getAvatarFile());
        }


        user.get().setAvatarFile(fileManager.get());
        appUserService.update(user.get());
        return new ResponseMessage(HttpStatus.OK, null);
    }

    @PutMapping("/updateUserCover")
    public ResponseMessage updateUserCover(@AuthenticationPrincipal AppUserDto userAuthen, @RequestParam("fileManagerId") Long fileManagerId) {

        if (userAuthen == null) {
            return new ResponseMessage(HttpStatus.UNAUTHORIZED, null);
        }

        Optional<AppUser> user = appUserService.findById(userAuthen.getId());
        Optional<FileManager> fileManager = fileManagerService.findById(fileManagerId);
        if (user.isEmpty() || fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        //delete old avatar file
        if (user.get().getCoverFile() != null) {
            fileManagerService.deleteFileBy(user.get().getCoverFile());
        }

        user.get().setCoverFile(fileManager.get());
        appUserService.update(user.get());
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
//    @GetMapping("/files/stream")
    public WebAsyncTask<ResponseEntity<StreamingResponseBody>> streamFileAsync(HttpServletRequest request,
                                                                               @RequestParam("path") String fileName,
                                                                               @RequestParam(defaultValue = "8192") int chunkSize) {

//        Optional<AppUserDto> userAuthen = jwtService.jwtVerify(
//                request.getHeader(ConstantData.ACCEPT_APIC_LIENT),
//                request.getHeader(ConstantData.AUTHORIZATION),
//                request.getHeader(ConstantData.X_SYNC_ACTIVE));
//        if (userAuthen.isEmpty()) {
//            throw this.responseErrorForbidden();
//        }
        log.info("streamFile:{}, chunkSize:{}", fileName, chunkSize);
        long timeout = 60 * 60 * 1000L; // 1 hour in ms
//        return streamFileByFilePath(fileName, chunkSize);
//        Callable<ResponseEntity<StreamingResponseBody>> callable = () -> streamRateLimitFileByFilePath(fileName, chunkSize);
        Callable<ResponseEntity<StreamingResponseBody>> callable = () -> streamFileByFilePath(fileName, chunkSize);
        WebAsyncTask<ResponseEntity<StreamingResponseBody>> webAsyncTask = new WebAsyncTask<>(timeout, callable);
//        webAsyncTask.onTimeout(() -> ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());
//        webAsyncTask.onError(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        return webAsyncTask;
    }

    @GetMapping("/files/stream")
    public ResponseEntity<StreamingResponseBody> streamFile(
            @RequestParam("path") String filePath,
            @RequestParam(defaultValue = "8192") int chunkSize,
            @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
            @RequestHeader(value = ConstantData.AUTHORIZATION) String authorization
    ) {

        log.info("streamFile:{}, chunkSize:{}, apiClientName:{}, authorization:{}", filePath, chunkSize, apiClientName, authorization);
        //        Optional<AppUserDto> userAuthen = jwtService.jwtVerify(
//                apiClientName,
//                request.getHeader(ConstantData.AUTHORIZATION),
//                request.getHeader(ConstantData.X_SYNC_ACTIVE));
        Optional<String> jwtSub = jwtService.getSubFromAuthorizationHeader(authorization, null);
        log.info("jwtSub :{} ", jwtSub.isPresent());
        if (jwtSub.isEmpty()) {
            throw this.responseErrorForbidden();
        }
        return streamFileByFilePath(filePath, chunkSize);
//        return streamRateLimitFileByFilePath(filePath, chunkSize);
    }

    //    @GetMapping("/files/stream")
    public void streamFileManual(
            @RequestParam("path") String filePath,
            @RequestParam(defaultValue = "8192") int chunkSize,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        log.info("Manual stream for file: {}", filePath);

        // Validate before setting any response headers
        File file = new File(appProperties.getUploadPath(), filePath);
        if (!file.exists() || !file.isFile()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        String mimeType = FileUtil.getMimeType(file);
        if (!isAllowedMimeType(mimeType)) {
            response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
            return;
        }

        // Set headers
        response.setContentType(mimeType);
        response.setContentLengthLong(file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.setHeader("Cache-Control", "no-cache");

        // Stream the file
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
             ServletOutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[chunkSize > 0 ? chunkSize : 8192];
            int bytesRead;
            long totalBytes = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                try {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;

                    // Flush periodically
                    if (totalBytes % (1024 * 1024) == 0) {
                        outputStream.flush();
                    }

                } catch (IOException e) {
                    log.info("Client disconnected after {} bytes for file: {}", totalBytes, filePath);
                    break; // Client disconnected, stop gracefully
                }
            }

            try {
                outputStream.flush();
                log.info("Successfully streamed {} bytes for file: {}", totalBytes, filePath);
            } catch (IOException e) {
                log.info("Client disconnected before final flush for file: {}", filePath);
            }

        } catch (FileNotFoundException e) {
            log.error("File not found: {}", filePath);
            if (!response.isCommitted()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (IOException e) {
            log.info("I/O error during manual streaming (likely client disconnect): {}", filePath);
            // Don't throw - this is likely a client disconnect
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private ResponseEntity<StreamingResponseBody> streamRateLimitFileByFilePath(String filePath, int chunkSize) {
        // Validate file path before any operations
        if (!isValidFilePath(filePath)) {
            return ResponseEntity.badRequest().build();
        }

        File file = new File(appProperties.getUploadPath(), filePath);

        validateStreamFile(file);
        String mimeType = "";
        try {
            mimeType = FileUtil.getMimeType(file);
        } catch (IOException ignore) {
        }

        // Prepare response headers
        HttpHeaders headers = prepareHeaders(file);

        // Create streaming response
        StreamingResponseBody responseBody = outputStream -> {
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[(chunkSize > 0 && chunkSize <= 1024 * 1024) ? chunkSize : 8192]; // default 8 KB if chunkSize not set
                int bytesRead;

                // 3 MB/s throttling
                RateLimiter rateLimiter = RateLimiter.create(appDefaultsProperties.getDataStreamLimit() * 1024 * 1024);
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    // Throttle download speed
                    rateLimiter.acquire(bytesRead);
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
                try {
                    outputStream.flush();
                } catch (IOException ignore) {
                    // client disconnected before flush
//                        log.error("Error streaming file {}: {}", fileName, ignore.getMessage());
                }
            } catch (IOException ignore) {
                // Log the error but do not re-throw. This is expected behavior for client disconnections.
                // The specific error might be "Broken pipe" or "Connection reset by peer."
                log.warn("Client disconnected while streaming file: {}", filePath);
            }
        };

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType(mimeType))
                .body(responseBody);
    }

    private ResponseEntity<StreamingResponseBody> streamFileByFilePath(String filePath, int chunkSize) {
        // Validate file path before any operations
        if (!isValidFilePath(filePath)) {
            return ResponseEntity.badRequest().build();
        }

        File file = new File(appProperties.getUploadPath(), filePath);
        validateStreamFile(file);
        String mimeType = "";
        try {
            mimeType = FileUtil.getMimeType(file);
        } catch (IOException ignore) {
        }

        // Prepare response headers
        HttpHeaders headers = prepareHeaders(file);
        StreamingResponseBody responseBody = outputStream -> {
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[(chunkSize > 0 && chunkSize <= 1024 * 1024) ? chunkSize : 8192];
                int bytesRead;

                int flushThreshold = 1024 * 1024;
                int bytesSinceFlush = 0;


                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    bytesSinceFlush += bytesRead;

                    if (bytesSinceFlush >= flushThreshold) {
                        outputStream.flush();
                        bytesSinceFlush = 0;
                    }
                }
                // Final flush after the loop
                try {
                    outputStream.flush();
                } catch (IOException ignore) {
                    // Client disconnected before final flush, so ignore.
                }
            } catch (IOException ignore) {
                log.warn("Client disconnected while streaming file: {}", filePath);
            }
        };

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType(mimeType))
                .body(responseBody);
    }

    //Video Streaming
    /* Streming video
    @GetMapping("/video/stream")
    public ResponseEntity<Resource> streamVideo(
            @RequestParam("path") String filePath,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) throws IOException {
        File file = new File(appProperties.getUploadPath(), filePath);

        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        long fileLength = file.length();
        long rangeStart = 0;
        long rangeEnd = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            rangeStart = Long.parseLong(ranges[0]);
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                rangeEnd = Long.parseLong(ranges[1]);
            }
        }

        if (rangeEnd > fileLength - 1) {
            rangeEnd = fileLength - 1;
        }

        long contentLength = rangeEnd - rangeStart + 1;
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        inputStream.skip(rangeStart);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
        headers.setContentLength(contentLength);

        return ResponseEntity.status(rangeHeader == null ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .contentType(MediaType.parseMediaType(mimeType))
                .body(new InputStreamResource(new LimitedInputStream(inputStream, contentLength) {
                    @Override
                    protected void raiseError(long l, long l1) throws IOException {

                    }
                }));
    }
  */
    @GetMapping("/video/stream")
    public ResponseEntity<StreamingResponseBody> streamVideo(
            @RequestParam("path") String filePath,
            @RequestParam(defaultValue = "8192") int chunkSize,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) {
        return streamingVideoByFilePath(filePath, chunkSize, rangeHeader);
    }

    @SuppressWarnings("UnstableApiUsage")
    private ResponseEntity<StreamingResponseBody> streamingVideoByFilePath(String filePath, int chunkSize, String rangeHeader) {
        if (!isValidFilePath(filePath)) {
            return ResponseEntity.badRequest().build();
        }

        File file = new File(appProperties.getUploadPath(), filePath);

        validateStreamFile(file);
        String mimeType = "";
        try {
            mimeType = FileUtil.getMimeType(file);
        } catch (IOException ignore) {
        }

        log.info("Streaming video mimeType: {}", mimeType);
        long fileLength = file.length();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        // Parse Range header
        long rangeStart = 0;
        long rangeEnd = fileLength - 1;
        boolean isPartial = false;

//        String rangeHeader = RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs
//                ? attrs.getRequest().getHeader(HttpHeaders.RANGE)
//                : null;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            isPartial = true;
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                rangeStart = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    rangeEnd = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException ignore) {
            }
            if (rangeEnd > fileLength - 1) {
                rangeEnd = fileLength - 1;
            }
            if (rangeStart > rangeEnd) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
            }

            headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
        }

        long contentLength = rangeEnd - rangeStart + 1;
        headers.setContentType(MediaType.parseMediaType(mimeType));
        headers.setContentLength(contentLength);

        long finalRangeStart = rangeStart;

        StreamingResponseBody responseBody = outputStream -> {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(finalRangeStart);

                byte[] buffer = new byte[(chunkSize > 0 && chunkSize <= 1024 * 1024) ? chunkSize : 8192];
                long bytesRemaining = contentLength;
                // Guava RateLimiter: 3 MB/s
                RateLimiter rateLimiter = RateLimiter.create(appDefaultsProperties.getDataStreamLimit() * 1024 * 1024);
                while (bytesRemaining > 0) {
                    int bytesToRead = (int) Math.min(buffer.length, bytesRemaining);
                    int bytesRead = raf.read(buffer, 0, bytesToRead);
                    if (bytesRead == -1) break;

                    // Acquire permits = number of bytes
                    rateLimiter.acquire(bytesRead);

                    outputStream.write(buffer, 0, bytesRead);
                    bytesRemaining -= bytesRead;
                }
                outputStream.flush();
            } catch (IOException ignore) {
            }
        };

        return (isPartial ? ResponseEntity.status(HttpStatus.PARTIAL_CONTENT) : ResponseEntity.ok())
                .headers(headers)
                .body(responseBody);
    }


    private void validateStreamFile(File file) {
        try {
            if (!isFileAccessAllowed(file)) {
                throw this.responseErrorForbidden();
            }
        } catch (IOException ignore) {
            throw this.responseErrorForbidden();
        }

        if (!file.exists() || !file.isFile()) {
            throw this.responseErrorNotfound();
        }

        String mimeType;
        try {
            mimeType = FileUtil.getMimeType(file);
        } catch (IOException e) {
            throw this.responseError(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        if (!isAllowedMimeType(mimeType)) {
            throw this.responseError(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
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
        } catch (IllegalArgumentException ignore) {
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
