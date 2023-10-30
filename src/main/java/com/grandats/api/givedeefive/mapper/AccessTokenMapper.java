package com.grandats.api.givedeefive.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface AccessTokenMapper {
    void updateLastestActive(@Param("lastestActive") LocalDateTime lastestActive,@Param("id") Long id);


}
