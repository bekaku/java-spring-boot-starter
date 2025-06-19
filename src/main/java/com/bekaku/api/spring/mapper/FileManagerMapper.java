package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.FileManagerDto;
import com.bekaku.api.spring.model.FileManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileManagerMapper {

    @Mappings({
            @Mapping(target = "fileMime", ignore = true),
    })
    FileManagerDto toDto(FileManager entity);

    @Mappings({
            @Mapping(target = "fileMime", ignore = true),
    })
    FileManager toEntity(FileManagerDto dto);
}
