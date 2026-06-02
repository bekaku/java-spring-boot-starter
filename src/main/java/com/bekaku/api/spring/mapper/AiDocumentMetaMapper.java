package com.bekaku.api.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.bekaku.api.spring.dto.AiDocumentMetaDto;
import com.bekaku.api.spring.model.AiDocumentMeta;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AiDocumentMetaMapper {
    AiDocumentMetaDto toDto(AiDocumentMeta entity);
    AiDocumentMeta toEntity(AiDocumentMetaDto dto);
}
