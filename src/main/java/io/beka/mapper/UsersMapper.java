package io.beka.mapper;

import io.beka.model.data.UserData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UsersMapper {
    Optional<UserData> findById(@Param("id") Long id);

    Optional<UserData> findByUsername(@Param("username") String username);

    Optional<UserData> findByEmail(@Param("email") String email);
}
