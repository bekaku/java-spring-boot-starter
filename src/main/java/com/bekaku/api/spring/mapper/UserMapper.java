package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.vo.Paging;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    List<UserDto> findAll(@Param("page") Paging page);
    Optional<UserDto> findById(@Param("id") Long id);
    Optional<UserDto> findByUsername(@Param("username") String username);
    Optional<UserDto> findByEmail(@Param("email") String email);
}
