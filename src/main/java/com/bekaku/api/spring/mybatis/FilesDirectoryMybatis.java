package com.bekaku.api.spring.mybatis;

import com.bekaku.api.spring.dto.FilesDirectoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface FilesDirectoryMybatis {

    Optional<FilesDirectoryDto> findById(@Param("id") Long id);

    Optional<FilesDirectoryDto> findByIdAndCrestedUserId(@Param("id") Long id, @Param("createdUserId") Long createdUserId);
}
