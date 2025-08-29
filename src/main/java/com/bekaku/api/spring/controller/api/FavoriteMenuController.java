package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.FavoriteMenuDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FavoriteMenu;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.FavoriteMenuService;
import com.bekaku.api.spring.specification.SearchSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(path = "/api/favoriteMenu")
@RestController
@RequiredArgsConstructor
public class FavoriteMenuController extends BaseApiController {

    private final FavoriteMenuService favoriteMenuService;
    private final AppUserService appUserService;
    private final I18n i18n;

    @GetMapping
    public ResponseListDto<FavoriteMenuDto> findAll(Pageable pageable) {
        SearchSpecification<FavoriteMenu> specification = new SearchSpecification<>(getSearchCriteriaList());
        return favoriteMenuService.findAllWithSearch(specification, getPageable(pageable, FavoriteMenu.getSort()));
    }

    @GetMapping("/findAllFavoriteMenu")
    public List<FavoriteMenuDto> findAllFavoriteMenu(@AuthenticationPrincipal AppUserDto userAuthen) {
        AppUser appUser = appUserService.findAndValidateAppUserBy(userAuthen);
        return favoriteMenuService.findAllByAppUser(appUser);
    }

    @PostMapping
    public FavoriteMenuDto create(@AuthenticationPrincipal AppUserDto userAuthen, @Valid @RequestBody FavoriteMenuDto dto) {
        AppUser appUser = appUserService.findAndValidateAppUserBy(userAuthen);
        Optional<FavoriteMenu> oldData = favoriteMenuService.findByAppUserAndUrl(appUser, dto.getUrl());
        if (oldData.isPresent()) {
            throw this.responseErrorDuplicate(i18n.getMessage("model.favoriteMenu"));
        }
        FavoriteMenu favoriteMenu = favoriteMenuService.convertDtoToEntity(dto);
        favoriteMenu.setAppUser(appUser);
        favoriteMenuService.save(favoriteMenu);
        return favoriteMenuService.convertEntityToDto(favoriteMenu);
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(@AuthenticationPrincipal AppUserDto userAuthen, @Valid @RequestBody FavoriteMenuDto dto) {
        AppUser appUser = appUserService.findAndValidateAppUserBy(userAuthen);
        Optional<FavoriteMenu> oldData = favoriteMenuService.findByAppUserAndUrl(appUser, dto.getUrl());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound(i18n.getMessage("model.favoriteMenu"));
        }
        favoriteMenuService.delete(oldData.get());
        return this.responseEntity(HttpStatus.OK);
    }
}
