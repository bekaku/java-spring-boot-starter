package com.bekaku.api.spring.service;
import com.bekaku.api.spring.dto.FilesDirectoryPathDto;

import com.bekaku.api.spring.model.FilesDirectoryPath;

import java.util.List;

public interface FilesDirectoryPathService extends BaseService<FilesDirectoryPath, FilesDirectoryPathDto> {
    List<FilesDirectoryPath> findAllByFolderId(Long folderId);

    void deleteByFolderId(Long folderId);

    void deleteByParentFolderId(Long parentFolderId);
}
