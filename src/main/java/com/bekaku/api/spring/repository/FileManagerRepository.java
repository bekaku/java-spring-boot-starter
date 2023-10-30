package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.FilesDirectory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileManagerRepository extends BaseRepository<FileManager, Long>, JpaSpecificationExecutor<FileManager> {

    List<FileManager> findAllByFilesDirectory(FilesDirectory filesDirectory);
}
