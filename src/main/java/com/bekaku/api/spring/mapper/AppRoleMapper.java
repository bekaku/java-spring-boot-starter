package com.bekaku.api.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.bekaku.api.spring.dto.AppRoleDto;
import com.bekaku.api.spring.model.AppRole;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppRoleMapper {
    AppRoleDto toDto(AppRole entity);
    AppRole toEntity(AppRoleDto dto);
}
