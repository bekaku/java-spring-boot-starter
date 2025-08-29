package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.AppRoleDto;
import com.bekaku.api.spring.model.AppRole;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    AppRoleDto toDto(AppRole entity);
    AppRole toEntity(AppRoleDto dto);
}
