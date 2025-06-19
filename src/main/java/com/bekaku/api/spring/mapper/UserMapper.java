package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User entity);
    User toEntity(UserDto dto);
}
