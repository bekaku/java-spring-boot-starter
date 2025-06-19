package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.PermissionDto;
import com.bekaku.api.spring.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {
    PermissionDto toDto(Permission entity);
    Permission toEntity(PermissionDto dto);
}
