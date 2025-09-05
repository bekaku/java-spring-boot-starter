package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.FileManagerDto;
import com.bekaku.api.spring.dto.ImageDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.mapper.FileManagerMapper;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.FilesDirectory;
import com.bekaku.api.spring.mybatis.FileManagerMybatis;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.repository.FileManagerRepository;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.FileUtil;
import com.bekaku.api.spring.vo.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class FileManagerServiceImpl implements FileManagerService {
    private final FileManagerRepository fileManagerRepository;
    private final FileManagerMybatis fileManagerMybatis;
    private final FileManagerMapper modelMapper;
    private final AppProperties appProperties;
    private final String DIRECTORY_MIME = "directory";

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<FileManagerDto> findAllWithPaging(Pageable pageable) {
        Page<FileManager> result = fileManagerRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<FileManagerDto> findAllWithSearch(SearchSpecification<FileManager> specification, Pageable pageable) {
        Page<FileManager> result = fileManagerRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<FileManagerDto> findAllBy(Specification<FileManager> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<FileManager> findAllPageSpecificationBy(Specification<FileManager> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<FileManager> findAllPageSearchSpecificationBy(SearchSpecification<FileManager> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<FileManagerDto> getListFromResult(Page<FileManager> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<FileManager> findAll() {
        return fileManagerRepository.findAll();
    }

    public FileManager save(FileManager fileManager) {
        return fileManagerRepository.save(fileManager);
    }

    @Override
    public FileManager update(FileManager fileManager) {
        return fileManagerRepository.save(fileManager);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FileManager> findById(Long id) {
        return fileManagerRepository.findById(id);
    }

    @Override
    public void delete(FileManager fileManager) {
        deleteFileFromPath(fileManager);
        fileManagerRepository.delete(fileManager);
    }

    @Override
    public void deleteById(Long id) {
        fileManagerRepository.deleteById(id);
    }

    @Override
    public FileManagerDto convertEntityToDto(FileManager fileManager) {
        boolean isImage = FileUtil.isImage(fileManager.getFileMime().getName());
        FileManagerDto dto = new FileManagerDto();
        dto.assign(fileManager.getId(),
                fileManager.getFileMime().getName(),
                fileManager.getOriginalFileName(),
                FileUtil.generateCdnPath(appProperties.getCdnForPublic(), fileManager.getFilePath(), null),
                isImage ? FileUtil.generateCdnPath(appProperties.getCdnForPublic(), FileUtil.generateThumbnailName(fileManager.getFilePath(), appProperties.getUploadImage().getThumbnailExname()), null) : null,
                FileUtil.humanReadableByteCountSI(fileManager.getFileSize()),
                fileManager.getCreatedDate(),
                fileManager.getCreatedDate(),
                false,
                isImage);

        return dto;
    }

    @Override
    public FileManager convertDtoToEntity(FileManagerDto fileManagerDto) {
        return modelMapper.toEntity(fileManagerDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FileManagerDto> findForPublicById(Long id) {
        return fileManagerMybatis.findForPublicById(id).map(this::setVoToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ImageDto> findImageDtoBy(Long id) {
        return fileManagerMybatis.findForPublicById(id)
                .map(fileManagerPublicVo -> getImageDtoBy(fileManagerPublicVo.getFileMime(), fileManagerPublicVo.getFilePath()));
    }

    public ImageDto getDefaultAvatar() {
        ImageDto imageDto = new ImageDto();
        String path = FileUtil.generateCdnPath(appProperties.getCdnForPublic(), "images/default-men-avatar.png", null);
        imageDto.setImage(path);
        imageDto.setThumbnail(path);
        return imageDto;
    }

    @Override
    public ImageDto getImageDtoBy(String fileMime, String path) {
        if (fileMime == null || path == null) {
            return null;
        }

        boolean isImage = FileUtil.isImage(fileMime);
        String url = FileUtil.generateCdnPath(appProperties.getCdnForPublic(), path, null);
        String thumbUrl = isImage ? FileUtil.generateCdnPath(appProperties.getCdnForPublic(), FileUtil.generateThumbnailName(path, appProperties.getUploadImage().getThumbnailExname()), null) : null;
        ImageDto imageDto = new ImageDto();
        imageDto.setImage(url);
        imageDto.setThumbnail(thumbUrl);
        return imageDto;
    }

    @Override
    public Optional<ImageDto> findImageDtoBy(FileManager fileManager) {
        if (fileManager == null) {
            return Optional.empty();
        }
        return Optional.of(getImageDtoBy(fileManager.getFileMime().getName(), fileManager.getFilePath()));
    }

    private FileManagerDto setVoToDto(FileManagerDto publicVo) {
//        FileManagerDto dto = new FileManagerDto();
        boolean isImage = FileUtil.isImage(publicVo.getFileMime());
        String path = !publicVo.isDirectoryFolder() ? FileUtil.generateCdnPath(appProperties.getCdnForPublic(), publicVo.getFilePath(), null) : null;
        String thumbnailPath = isImage ? FileUtil.generateCdnPath(appProperties.getCdnForPublic(), FileUtil.generateThumbnailName(publicVo.getFilePath(), appProperties.getUploadImage().getThumbnailExname()), null) : null;
//        dto.assign(publicVo.getId(),
//                publicVo.getFileMime(),
//                publicVo.getFileName(),
//                path,
//                thumbnailPath,
//                FileUtil.humanReadableByteCountSI(publicVo.getFileSize()),
//                publicVo.getCreatedDate(),
//                publicVo.getUpdatedDate(),
//                publicVo.isDirectoryFolder());
//
//        return dto;
        publicVo.setFilePath(path);
        publicVo.setFileThumbnailPath(thumbnailPath);
        publicVo.setFileSize(FileUtil.humanReadableByteCountSI(publicVo.getFileSizeNo()));
        publicVo.setImage(isImage);
        return publicVo;
    }

    @Override
    public FileManagerDto setVoToDto(FilesDirectory filesDirectory) {
        FileManagerDto dto = new FileManagerDto();
        dto.assign(filesDirectory.getId(),
                DIRECTORY_MIME,
                filesDirectory.getName(),
                null,
                null,
                FileUtil.humanReadableByteCountSI(filesDirectory.getFileSize()),
                filesDirectory.getCreatedDate(),
                filesDirectory.getLatestUpdated(),
                true,
                false);
        return dto;
    }

    public FileManagerDto setEntityToDto(FileManager f) {
        FileManagerDto dto = new FileManagerDto();
        boolean isImage = FileUtil.isImage(f.getFileMime().getName());

        dto.assign(f.getId(),
                f.getFileMime().getName(),
                f.getOriginalFileName(),
                FileUtil.generateCdnPath(appProperties.getCdnForPublic(), f.getFilePath(), null),
                isImage ? FileUtil.generateCdnPath(appProperties.getCdnForPublic(), FileUtil.generateThumbnailName(f.getFilePath(), appProperties.getUploadImage().getThumbnailExname()), null) : null,
                FileUtil.humanReadableByteCountSI(f.getFileSize()),
                f.getCreatedDate(),
                f.getCreatedDate(),
                false,
                isImage);
        return dto;
    }

    @Override
    public List<FileManagerDto> findAllFolderAndFileByParentFolderAndOwnerId(Paging page, Long parentDirectoryId, Long owner) {
        return fileManagerMybatis.findAllFolderAndFileByParentFolderAndOwnerId(page, parentDirectoryId, owner).stream()
                .map(this::setVoToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileManagerDto> findAllFolderByParentFolderAndOwnerId(Paging page, Long parentDirectoryId, Long ownerId) {
        return fileManagerMybatis.findAllFolderByParentFolderAndOwnerId(page, parentDirectoryId, ownerId).stream()
                .map(this::setVoToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileManagerDto> findAllFileByParentFolderAndOwnerId(Paging page, Long parentDirectoryId, Long ownerId) {
        return fileManagerMybatis.findAllFileByParentFolderAndOwnerId(page, parentDirectoryId, ownerId).stream()
                .map(this::setVoToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<FileManager> findAllByFilesDirectory(FilesDirectory filesDirectory) {
        return fileManagerRepository.findAllByFilesDirectory(filesDirectory);
    }

    @Override
    public void deleteAllFileByFilesDirectory(FilesDirectory filesDirectory) {
        Page<FileManager> page;
        int pageNumber = 0;
        do {
            page = fileManagerRepository.findAllByFilesDirectory(filesDirectory, PageRequest.of(pageNumber++, 20));
            page.forEach(this::deleteFileBy);
        } while (!page.isEmpty());
//        List<FileManager> fileManagers = fileManagerRepository.findAllByFilesDirectory(filesDirectory);
//        if (!fileManagers.isEmpty()) {
//            for (FileManager f : fileManagers) {
//                deleteFileBy(f);
//            }
//        }
    }

    @Override
    public void deleteFileBy(FileManager fileManager) {
        deleteFileFromPath(fileManager);
        delete(fileManager);
    }

    private void deleteFileFromPath(FileManager fileManager) {
        String filePath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), fileManager.getFilePath());
        try {
            Files.deleteIfExists(Path.of(filePath));
            if (fileManager.isImage()) {
                String fileThumbnailPath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), FileUtil.generateThumbnailName(fileManager.getFilePath(), appProperties.getUploadImage().getThumbnailExname()));
                Files.deleteIfExists(Path.of(fileThumbnailPath));
            }
            fileManager.setFilesDirectory(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
