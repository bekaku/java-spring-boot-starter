package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AppRoleDto;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.service.AppRoleService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.validator.RoleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class AppRoleControllerTest {

    @Mock
    AppRoleService appRoleService;
    @Mock
    PermissionService permissionService;
    @Mock
    AppUserService appUserService;
    @Mock
    I18n i18n;
    @Mock
    RoleValidator roleValidator;

    private AppRoleController controller;

    @BeforeEach
    void setUp() {
        controller = new AppRoleController(appRoleService, permissionService, appUserService, i18n, roleValidator);
    }

    @Test
    void createValidatesRoleAsNewBeforeSaving() {
        AppRoleDto dto = new AppRoleDto().setName("Auditor").setActive(true);
        AppRole role = new AppRole("Auditor", true);
        when(appRoleService.convertDtoToEntity(dto)).thenReturn(role);

        assertThat(controller.create(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        InOrder order = inOrder(roleValidator, appRoleService);
        order.verify(roleValidator).validateCreate(role);
        order.verify(appRoleService).save(role);
        verify(roleValidator, never()).validateUpdate(any());
    }

    @Test
    void updateValidatesRoleAsExistingBeforeSaving() {
        AppRole role = new AppRole("Editor", true);
        role.setId(2L);
        AppRoleDto dto = new AppRoleDto().setName("Senior Editor").setActive(true);
        when(appRoleService.findById(2L)).thenReturn(Optional.of(role));

        assertThat(controller.update(dto, 2L).getStatusCode()).isEqualTo(HttpStatus.OK);

        InOrder order = inOrder(roleValidator, appRoleService);
        order.verify(roleValidator).validateUpdate(role);
        order.verify(appRoleService, atLeastOnce()).update(role);
        verify(roleValidator, never()).validateCreate(any());
    }
}
