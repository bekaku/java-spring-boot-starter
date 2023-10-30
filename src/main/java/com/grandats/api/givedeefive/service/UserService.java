package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.UserDto;
import com.grandats.api.givedeefive.model.User;
import com.grandats.api.givedeefive.vo.Paging;

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


    void requireTheSameUser(Long leftUserId, Long rightUserId);
    void requireTheSameUser(User leftUser, User rightUser);

}
