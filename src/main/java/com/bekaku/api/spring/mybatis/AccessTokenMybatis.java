package com.bekaku.api.spring.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface AccessTokenMybatis {
    void updateLastestActive(@Param("lastestActive") LocalDateTime lastestActive,@Param("id") Long id);


}
