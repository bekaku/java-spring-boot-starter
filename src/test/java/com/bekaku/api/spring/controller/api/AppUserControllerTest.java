package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.UserRegisterRequest;
import com.bekaku.api.spring.dto.UserUpdateRequest;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.AppRoleService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.EncryptService;
import com.bekaku.api.spring.service.FavoriteMenuService;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserControllerTest {

    @Mock
    AppUserService appUserService;
    @Mock
    AppRoleService appRoleService;
    @Mock
    EncryptService encryptService;
    @Mock
    FileManagerService fileManagerService;
    @Mock
    AccessTokenService accessTokenService;
    @Mock
    UserValidator userValidator;
    @Mock
    I18n i18n;
    @Mock
    PermissionService permissionService;
    @Mock
    ApiClientService apiClientService;
    @Mock
    JwtService jwtService;
    @Mock
    FavoriteMenuService favoriteMenuService;
    @Mock
    JwtProperties jwtProperties;

    private AppUserController controller;

    @BeforeEach
    void setUp() {
        controller = new AppUserController(appUserService, appRoleService, encryptService, fileManagerService,
                accessTokenService, userValidator, i18n, permissionService, apiClientService, jwtService,
                favoriteMenuService, jwtProperties);
    }

    @Test
    void createValidatesUserAsNewBeforeSaving() {
        UserRegisterRequest dto = new UserRegisterRequest().setPassword("Secret#123");
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");

        assertThat(controller.create(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ArgumentCaptor<AppUser> validated = ArgumentCaptor.forClass(AppUser.class);
        InOrder order = inOrder(userValidator, appUserService);
        order.verify(userValidator).validateCreate(validated.capture());
        order.verify(appUserService).save(validated.getValue());
        assertThat(validated.getValue().getEmail()).isEqualTo("alice@example.com");
        verify(userValidator, never()).validateUpdate(any());
    }

    @Test
    void updateUserValidatesUserAsExistingBeforeSaving() {
        AppUser user = new AppUser();
        user.setId(5L);
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        UserUpdateRequest dto = new UserUpdateRequest();
        dto.setUsername("bob");
        dto.setEmail("bob.new@example.com");
        when(appUserService.findById(5L)).thenReturn(Optional.of(user));

        assertThat(controller.updateUser(dto, 5L).getStatusCode()).isEqualTo(HttpStatus.OK);

        InOrder order = inOrder(userValidator, appUserService);
        order.verify(userValidator).validateUpdate(user);
        order.verify(appUserService, atLeastOnce()).update(user);
        verify(userValidator, never()).validateCreate(any());
    }
}
