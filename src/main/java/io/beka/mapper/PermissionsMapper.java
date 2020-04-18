package io.beka.mapper;

import io.beka.model.Page;
import io.beka.model.entity.Permissions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionsMapper {
    List<Permissions> findAllWithPaging(@Param("page") Page page);
}
