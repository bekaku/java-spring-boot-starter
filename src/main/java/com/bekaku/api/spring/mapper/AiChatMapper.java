package com.bekaku.api.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.bekaku.api.spring.dto.AiChatDto;
import com.bekaku.api.spring.model.AiChat;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AiChatMapper {
    AiChatDto toDto(AiChat entity);
    AiChat toEntity(AiChatDto dto);
}
