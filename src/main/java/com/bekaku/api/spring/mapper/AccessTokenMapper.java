package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.dto.AccessTokenDto;
import com.bekaku.api.spring.model.AccessToken;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccessTokenMapper {

    //    @Mappings({
//            @Mapping(source = "id", target = "id"),
//            @Mapping(source = "docNo", target = "docNo"),
//            @Mapping(source = "referenceNo", target = "referenceNo"),
//            @Mapping(source = "docName", target = "docName"),
//            @Mapping(source = "latestApproved", target = "latestApproved"),
//            @Mapping(source = "keywordSearch", target = "keywordSearch"),
//            @Mapping(source = "createLocked", target = "createLocked"),
//            @Mapping(target = "wiDocumentTypeDto", ignore = true),
//            @Mapping(target = "organizationOwner", ignore = true),
//            @Mapping(target = "wiDataTaggingItems", ignore = true),
//            @Mapping(target = "wiVersion", ignore = true),
//            @Mapping(target = "fileSelectedIds", ignore = true),
//            @Mapping(target = "newVersionBy", ignore = true),
//    })
    AccessTokenDto toDto(AccessToken entity);
    AccessToken toEntity(AccessTokenDto dto);
}
