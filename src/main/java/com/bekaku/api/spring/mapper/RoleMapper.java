package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.RoleDto;
import com.bekaku.api.spring.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    RoleDto toDto(Role entity);
    Role toEntity(RoleDto dto);
}
