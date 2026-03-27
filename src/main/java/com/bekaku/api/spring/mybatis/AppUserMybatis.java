package com.bekaku.api.spring.mybatis;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.vo.Paging;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AppUserMybatis {

    List<AppUserDto> findAll(@Param("page") Paging page);
    Optional<AppUserDto> findById(@Param("id") Long id);
    Optional<AppUserDto> findByUsername(@Param("username") String username);
    Optional<AppUserDto> findByEmail(@Param("email") String email);
    Optional<AppUserDto> findByAccessTokenKey(@Param("token") String token);
}
