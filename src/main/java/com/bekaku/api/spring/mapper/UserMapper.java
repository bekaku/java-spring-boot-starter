package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    AppUserDto toDto(AppUser entity);
    AppUser toEntity(AppUserDto dto);
}
