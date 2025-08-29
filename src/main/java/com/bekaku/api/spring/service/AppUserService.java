package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.vo.Paging;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AppUserService extends BaseService<AppUser, AppUserDto> {
    Optional<AppUser> findByUUID(String salt);
    void updatePasswordBy(AppUser appUser, String password);
    //Repository
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findActiveByEmailOrUserName(String email);

    //Mapper
    List<AppUserDto> findAllUserData(Paging page);

    Optional<AppUserDto> findUserDataById(Long id);

    Optional<AppUserDto> findUserDataByUsername(String username);

    Optional<AppUserDto> findUserDataByEmail(String email);


    void requireTheSameUser(Long leftUserId, Long rightUserId);

    void requireTheSameUser(AppUser leftAppUser, AppUser rightAppUser);

    CompletableFuture<String> processAsyncTask();

    Page<AppUser> findAllPageBy(Pageable pageable);

    AppUser findAndValidateAppUserBy(AppUserDto userAuthen);
}
