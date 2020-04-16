package io.beka.dao;

import io.beka.model.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.security.Permission;
import java.util.List;

@Mapper
public interface PermissionsMapper {
    List<Permission> allPaging(@Param("page") Page page);
}
