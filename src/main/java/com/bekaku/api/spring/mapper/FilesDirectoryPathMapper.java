package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.FilesDirectoryPathDto;
import com.bekaku.api.spring.model.FilesDirectoryPath;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FilesDirectoryPathMapper {
    FilesDirectoryPathDto toDto(FilesDirectoryPath entity);
    FilesDirectoryPath toEntity(FilesDirectoryPathDto dto);
}
