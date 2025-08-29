package com.bekaku.api.spring.service;
import com.bekaku.api.spring.dto.FavoriteMenuDto;

import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FavoriteMenu;

import java.util.List;
import java.util.Optional;

public interface FavoriteMenuService extends BaseService<FavoriteMenu, FavoriteMenuDto> {

    List<FavoriteMenuDto> findAllByAppUser(AppUser appUser);

    Optional<FavoriteMenu> findByAppUserAndUrl(AppUser appUser, String url);
}
