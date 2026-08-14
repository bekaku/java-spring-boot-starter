package com.bekaku.api.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.bekaku.api.spring.dto.AiDocumentMetaDto;
import com.bekaku.api.spring.model.AiDocumentMeta;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AiDocumentMetaMapper {

    @Mapping(target = "fileMime", source = "fileMime.name")
    AiDocumentMetaDto toDto(AiDocumentMeta entity);

    @Mapping(target = "fileMime", ignore = true)
    AiDocumentMeta toEntity(AiDocumentMetaDto dto);
}
