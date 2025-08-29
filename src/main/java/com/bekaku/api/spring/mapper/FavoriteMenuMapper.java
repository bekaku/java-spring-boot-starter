package com.bekaku.api.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.bekaku.api.spring.dto.FavoriteMenuDto;
import com.bekaku.api.spring.model.FavoriteMenu;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoriteMenuMapper {
    FavoriteMenuDto toDto(FavoriteMenu entity);
    FavoriteMenu toEntity(FavoriteMenuDto dto);
}
