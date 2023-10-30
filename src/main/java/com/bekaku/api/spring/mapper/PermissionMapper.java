package com.bekaku.api.spring.mapper;

import com.bekaku.api.spring.vo.Paging;
import com.bekaku.api.spring.model.Permission;
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
