package com.bekaku.api.spring.service;
import com.bekaku.api.spring.dto.FilesDirectoryDto;

import com.bekaku.api.spring.model.FilesDirectory;

import java.util.List;
import java.util.Optional;

public interface FilesDirectoryService extends BaseService<FilesDirectory, FilesDirectoryDto> {
    List<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent);

    Optional<FilesDirectoryDto> findDirectoryById(Long id);
}
