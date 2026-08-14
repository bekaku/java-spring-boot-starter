package com.bekaku.api.spring.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.AiDocumentMeta;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiDocumentMetaRepository extends BaseRepository<AiDocumentMeta,Long>, JpaSpecificationExecutor<AiDocumentMeta> {

    List<AiDocumentMeta> findByActiveTrueOrderByFileNameAsc();

    Optional<AiDocumentMeta> findByIdAndActiveTrue(Long id);

    @Query("select d from AiDocumentMeta d where d.active = true and lower(d.fileName) like lower(concat('%', :keyword, '%'))")
    List<AiDocumentMeta> searchActiveByFileName(@Param("keyword") String keyword);

    Optional<AiDocumentMeta> findByFileName(String fileName);

    @Query("SELECT d.fileName FROM AiDocumentMeta d WHERE d.active = true")
    List<String> findAllActiveFileNames();
}
