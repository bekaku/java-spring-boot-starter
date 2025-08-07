package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.vo.Paging;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserService extends BaseService<User, UserDto> {
    Optional<User> findByUUID(String salt);
    void updatePasswordBy(User user, String password);
    //Repository
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    Optional<User> findActiveByEmailOrUserName(String email);

    //Mapper
    List<UserDto> findAllUserData(Paging page);

    Optional<UserDto> findUserDataById(Long id);

    Optional<UserDto> findUserDataByUsername(String username);

    Optional<UserDto> findUserDataByEmail(String email);


    void requireTheSameUser(Long leftUserId, Long rightUserId);

    void requireTheSameUser(User leftUser, User rightUser);

    CompletableFuture<String> processAsyncTask();

    Page<User> findAllPageBy(Pageable pageable);
}
