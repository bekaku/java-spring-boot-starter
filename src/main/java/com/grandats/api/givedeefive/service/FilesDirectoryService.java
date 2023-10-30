package com.grandats.api.givedeefive.service;
import com.grandats.api.givedeefive.dto.FilesDirectoryDto;

import com.grandats.api.givedeefive.model.FilesDirectory;

import java.util.List;
import java.util.Optional;

public interface FilesDirectoryService extends BaseService<FilesDirectory, FilesDirectoryDto> {
    List<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent);

    Optional<FilesDirectoryDto> findDirectoryById(Long id);
}
