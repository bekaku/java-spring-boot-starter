package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FilesDirectory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilesDirectoryRepository extends BaseRepository<FilesDirectory, Long>, JpaSpecificationExecutor<FilesDirectory> {

    List<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent);
//    @Query("SELECT * FROM FilesDirectory f WHERE f.filesDirectoryParent = :filesDirectoryParent")
//    List<FilesDirectory> findAllByFilesDirectoryParent(@Param(value = "filesDirectoryParent") FilesDirectory filesDirectoryParent);

    Optional<FilesDirectory> findByNameAndFilesDirectoryParent(String name, FilesDirectory filesDirectoryParent);

    Optional<FilesDirectory> findByNameAndOwnerAndFilesDirectoryParentIsNull(String name, AppUser owner);

    Optional<FilesDirectory> findByOwnerAndFilesDirectoryParent(AppUser owner, FilesDirectory filesDirectoryParent);

    Optional<FilesDirectory> findByOwnerAndId(AppUser owner, Long id);

}
