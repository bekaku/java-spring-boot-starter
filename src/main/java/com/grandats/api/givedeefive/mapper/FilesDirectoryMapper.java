package com.grandats.api.givedeefive.mapper;

import com.grandats.api.givedeefive.dto.FilesDirectoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface FilesDirectoryMapper {

    Optional<FilesDirectoryDto> findById(@Param("id") Long id);

    Optional<FilesDirectoryDto> findByIdAndCrestedUserId(@Param("id") Long id, @Param("createdUserId") Long createdUserId);
}
