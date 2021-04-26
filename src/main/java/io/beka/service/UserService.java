package io.beka.service;

import io.beka.vo.Paging;
import io.beka.dto.UserDto;
import io.beka.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<User, UserDto> {
    //Repository
     Optional<User> findByEmail(String email);
     Optional<User> findByUsername(String username);

    //Mapper
     List<UserDto> findAllUserData(Paging page);
     Optional<UserDto> findUserDataById(Long id);
     Optional<UserDto> findUserDataByUsername(String username);
     Optional<UserDto> findUserDataByEmail(String email);
}
