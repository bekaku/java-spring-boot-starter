package io.beka.mapper;

import io.beka.model.Page;
import io.beka.model.data.UserData;
import io.beka.model.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    List<UserData> findAll(@Param("page") Page page);

    Optional<UserData> findById(@Param("id") Long id);

    Optional<UserData> findByUsername(@Param("username") String username);

    Optional<UserData> findByEmail(@Param("email") String email);
}
