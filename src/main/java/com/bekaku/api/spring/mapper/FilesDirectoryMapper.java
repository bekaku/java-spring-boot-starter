package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.FilesDirectoryDto;
import com.bekaku.api.spring.model.FilesDirectory;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FilesDirectoryMapper {
    FilesDirectoryDto toDto(FilesDirectory entity);
    FilesDirectory toEntity(FilesDirectoryDto dto);
}
