package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.vo.Paging;

import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<User, UserDto> {

    void updatePasswordBy(User user, String password);
    //Repository
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    //Mapper
    List<UserDto> findAllUserData(Paging page);

    Optional<UserDto> findUserDataById(Long id);

    Optional<UserDto> findUserDataByUsername(String username);

    Optional<UserDto> findUserDataByEmail(String email);


    void requireTheSameUser(Long leftUserId, Long rightUserId);
    void requireTheSameUser(User leftUser, User rightUser);

}
