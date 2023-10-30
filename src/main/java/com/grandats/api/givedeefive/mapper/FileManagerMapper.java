package com.grandats.api.givedeefive.mapper;

import com.grandats.api.givedeefive.vo.FileManagerPublicVo;
import com.grandats.api.givedeefive.vo.Paging;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FileManagerMapper {

    Optional<FileManagerPublicVo> findForPublicById(@Param("id") Long id);

    List<FileManagerPublicVo> findAllFolderAndFileByParentFolder(@Param("page") Paging page, Long parentDirectoryId);
}
