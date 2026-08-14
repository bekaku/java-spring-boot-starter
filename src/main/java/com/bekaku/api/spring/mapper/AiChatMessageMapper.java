package com.bekaku.api.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.bekaku.api.spring.dto.AiChatMessageDto;
import com.bekaku.api.spring.model.AiChatMessage;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AiChatMessageMapper {
    AiChatMessageDto toDto(AiChatMessage entity);
    AiChatMessage toEntity(AiChatMessageDto dto);
}
