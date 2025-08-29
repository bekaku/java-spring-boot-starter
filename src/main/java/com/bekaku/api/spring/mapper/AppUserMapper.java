package com.bekaku.api.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.model.AppUser;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppUserMapper {
    AppUserDto toDto(AppUser entity);
    AppUser toEntity(AppUserDto dto);
}
