package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.AppUser;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.FavoriteMenu;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteMenuRepository extends BaseRepository<FavoriteMenu,Long>, JpaSpecificationExecutor<FavoriteMenu> {

    List<FavoriteMenu> findAllByAppUser(AppUser appUser);

    Optional<FavoriteMenu> findByAppUserAndUrl(AppUser appUser, String url);
}
