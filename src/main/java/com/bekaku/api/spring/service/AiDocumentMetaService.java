package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.AiDocumentMetaDto;
import com.bekaku.api.spring.model.AiDocumentMeta;

import java.util.List;
import java.util.Optional;

public interface AiDocumentMetaService extends BaseService<AiDocumentMeta, AiDocumentMetaDto> {

    Optional<AiDocumentMeta> findByFileName(String fileName);

    List<String> findAllActiveFileNames();
}
