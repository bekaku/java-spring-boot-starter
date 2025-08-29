package com.bekaku.api.spring.mybatis;

import com.bekaku.api.spring.vo.FileManagerPublicVo;
import com.bekaku.api.spring.vo.Paging;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FileManagerMybatis {

    Optional<FileManagerPublicVo> findForPublicById(@Param("id") Long id);

    List<FileManagerPublicVo> findAllFolderAndFileByParentFolder(@Param("page") Paging page, @Param("parentDirectoryId") Long parentDirectoryId, @Param("ownerId") Long ownerId);
}
