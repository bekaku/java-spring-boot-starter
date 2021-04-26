package io.beka.service;

import io.beka.vo.Paging;
import io.beka.dto.UserData;
import io.beka.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<User, UserData> {
    //Repository
     Optional<User> findByEmail(String email);
     Optional<User> findByUsername(String username);

    //Mapper
     List<UserData> findAllUserData(Paging page);
     Optional<UserData> findUserDataById(Long id);
     Optional<UserData> findUserDataByUsername(String username);
     Optional<UserData> findUserDataByEmail(String email);
}
