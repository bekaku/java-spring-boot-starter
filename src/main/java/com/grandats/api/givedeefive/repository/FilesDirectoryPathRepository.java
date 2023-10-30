package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.FilesDirectoryPath;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesDirectoryPathRepository extends BaseRepository<FilesDirectoryPath,Long>, JpaSpecificationExecutor<FilesDirectoryPath> {

    @Query("SELECT p FROM FilesDirectoryPath p WHERE p.filesDirectoryPathId.filesDirectory = :folderId")
    List<FilesDirectoryPath> findAllByFolderId(@Param(value = "folderId") Long folderId);

    @Modifying
    @Query("DELETE from FilesDirectoryPath p WHERE p.filesDirectoryPathId.filesDirectory = :folderId")
    void deleteByFolderId(@Param("folderId") Long folderId);

    @Modifying
    @Query("DELETE from FilesDirectoryPath p WHERE p.filesDirectoryPathId.filesDirectoryParent = :parentFolderId")
    void deleteByParentFolderId(@Param("parentFolderId") Long parentFolderId);

}
