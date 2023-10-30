package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.FileManagerDto;
import com.grandats.api.givedeefive.vo.Paging;

import com.grandats.api.givedeefive.dto.ImageDto;
import com.grandats.api.givedeefive.model.FileManager;
import com.grandats.api.givedeefive.model.FilesDirectory;

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
