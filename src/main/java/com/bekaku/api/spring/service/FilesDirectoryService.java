package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.FilesDirectoryDto;

import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FilesDirectory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface FilesDirectoryService extends BaseService<FilesDirectory, FilesDirectoryDto> {
    List<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent);

    Optional<FilesDirectoryDto> findDirectoryById(Long id);

    Optional<FilesDirectoryDto> findByIdAndOwnerId(Long id, Long ownerId);


    void validateFolderOwner(AppUser appUser, FilesDirectory folder);

    FilesDirectory validateFolderOwnerAndGetBy(AppUser appUser, Long folderID);

    void validateDuplicateName(AppUser appUser, String name, FilesDirectory filesDirectoryParent);
}
