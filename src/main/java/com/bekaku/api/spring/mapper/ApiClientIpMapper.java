package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.ApiClientIpDto;
import com.bekaku.api.spring.model.ApiClientIp;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApiClientIpMapper {
    ApiClientIpDto toDto(ApiClientIp entity);
    ApiClientIp toEntity(ApiClientIpDto dto);
}
