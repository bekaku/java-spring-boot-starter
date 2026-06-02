package com.bekaku.api.spring.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.AiDocumentMeta;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiDocumentMetaRepository extends BaseRepository<AiDocumentMeta,Long>, JpaSpecificationExecutor<AiDocumentMeta> {

    Optional<AiDocumentMeta> findByFileName(String fileName);

    @Query("SELECT d.fileName FROM AiDocumentMeta d WHERE d.isActive = true")
    List<String> findAllActiveFileNames();
}
