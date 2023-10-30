package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.FileManagerDto;
import com.bekaku.api.spring.vo.Paging;

import com.bekaku.api.spring.dto.ImageDto;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.FilesDirectory;

import java.util.List;
import java.util.Optional;

public interface FileManagerService extends BaseService<FileManager, FileManagerDto> {
    Optional<FileManagerDto> findForPublicById(Long id);

    FileManagerDto setEntityToDto(FileManager f);

    Optional<ImageDto> findImageDtoBy(Long id);

    Optional<ImageDto> findImageDtoBy(FileManager fileManager);

    ImageDto getDefaultAvatar();

    ImageDto getImageDtoBy(String fileMime, String path);

    List<FileManagerDto> findAllFolderAndFileByParentFolder(Paging page, Long parentDirectoryId);

    List<FileManager> findAllByFilesDirectory(FilesDirectory filesDirectory);

    void deleteAllFileByFilesDirectory(FilesDirectory filesDirectory);

    void deleteFileBy(FileManager fileManager);
}
