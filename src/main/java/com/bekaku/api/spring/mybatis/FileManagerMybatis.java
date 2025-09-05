package com.bekaku.api.spring.mybatis;

import com.bekaku.api.spring.dto.FileManagerDto;
import com.bekaku.api.spring.vo.Paging;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FileManagerMybatis {

    Optional<FileManagerDto> findForPublicById(@Param("id") Long id);

    List<FileManagerDto> findAllFolderAndFileByParentFolderAndOwnerId(@Param("page") Paging page,
                                                                 @Param("parentDirectoryId") Long parentDirectoryId,
                                                                 @Param("ownerId") Long ownerId);
    List<FileManagerDto> findAllFolderByParentFolderAndOwnerId(@Param("page") Paging page,
                                                                 @Param("parentDirectoryId") Long parentDirectoryId,
                                                                 @Param("ownerId") Long ownerId);
    List<FileManagerDto> findAllFileByParentFolderAndOwnerId(@Param("page") Paging page,
                                                                 @Param("parentDirectoryId") Long parentDirectoryId,
                                                                 @Param("ownerId") Long ownerId);
}
