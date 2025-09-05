package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.FilesDirectoryDto;

import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FilesDirectory;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FilesDirectoryService extends BaseService<FilesDirectory, FilesDirectoryDto> {



    List<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent);

    Page<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent, Pageable pageable);

    Optional<FilesDirectoryDto> findDirectoryById(Long id);

    Optional<FilesDirectory> findByIdAndOwnerId(Long id, Long appUserId);
    Optional<FilesDirectoryDto> findDtoByIdAndOwnerId(Long id, Long ownerId);


    void validateFolderOwner(AppUser appUser, FilesDirectory folder);

    FilesDirectory validateFolderOwnerAndGetBy(AppUser appUser, Long folderID);

    void validateDuplicateName(AppUser appUser, String name, FilesDirectory filesDirectoryParent);
}
