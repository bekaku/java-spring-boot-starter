package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.ApiClientDto;
import com.bekaku.api.spring.model.ApiClient;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApiClientMapper {
    ApiClientDto toDto(ApiClient entity);
    ApiClient toEntity(ApiClientDto dto);
}
