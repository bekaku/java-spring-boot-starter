package io.beka.mapper;

import io.beka.dto.Paging;
import io.beka.model.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {
    List<Permission> findAllWithPaging(@Param("page") Paging page);
}
