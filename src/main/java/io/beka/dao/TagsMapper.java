package io.beka.dao;

import io.beka.model.Page;
import io.beka.model.data.Tags;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagsMapper {
    List<Tags> allPaging(@Param("page") Page page);
}
