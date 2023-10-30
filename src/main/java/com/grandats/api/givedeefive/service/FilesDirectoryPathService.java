package com.grandats.api.givedeefive.service;
import com.grandats.api.givedeefive.dto.FilesDirectoryPathDto;

import com.grandats.api.givedeefive.model.FilesDirectoryPath;

import java.util.List;

public interface FilesDirectoryPathService extends BaseService<FilesDirectoryPath, FilesDirectoryPathDto> {
    List<FilesDirectoryPath> findAllByFolderId(Long folderId);

    void deleteByFolderId(Long folderId);

    void deleteByParentFolderId(Long parentFolderId);
}
