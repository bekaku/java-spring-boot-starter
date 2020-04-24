package io.beka.mapper;

import io.beka.model.Page;
import io.beka.model.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {
    List<Permission> findAllWithPaging(@Param("page") Page page);
}
