package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.FileManager;
import com.grandats.api.givedeefive.model.FilesDirectory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileManagerRepository extends BaseRepository<FileManager, Long>, JpaSpecificationExecutor<FileManager> {

    List<FileManager> findAllByFilesDirectory(FilesDirectory filesDirectory);
}
