package com.grandats.api.givedeefive.mapper;

import com.grandats.api.givedeefive.vo.Paging;
import com.grandats.api.givedeefive.model.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {
    /**
     *
     * @param page
     * Pa
     */
    List<Permission> findAllWithPaging(@Param("page") Paging page);
}
