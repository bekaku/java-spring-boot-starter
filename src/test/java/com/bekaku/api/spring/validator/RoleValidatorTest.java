package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.service.AppRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleValidatorTest {

    @Mock
    AppRoleService appRoleService;
    @Mock
    I18n i18n;

    private RoleValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RoleValidator(appRoleService, i18n);
    }

    @Test
    void validateCreateRejectsDuplicateNameWithBadRequest() {
        when(appRoleService.findByName("Admin")).thenReturn(Optional.of(role(1L, "Admin")));
        when(i18n.getMessage("error.validateDuplicate", "Admin")).thenReturn("Admin already exists.");
        when(i18n.getMessage("error.error")).thenReturn("Error");

        assertThatThrownBy(() -> validator.validateCreate(role(null, "Admin")))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getApiError().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getApiError().getMessage()).isEqualTo("Error");
                    assertThat(exception.getApiError().getErrors()).containsExactly("Admin already exists.");
                });
    }

    @Test
    void validateUpdateRejectsRenameToExistingName() {
        when(appRoleService.findById(2L)).thenReturn(Optional.of(role(2L, "Editor")));
        when(appRoleService.findByName("Admin")).thenReturn(Optional.of(role(1L, "Admin")));
        when(i18n.getMessage("error.validateDuplicate", "Admin")).thenReturn("Admin already exists.");
        when(i18n.getMessage("error.error")).thenReturn("Error");

        assertThatThrownBy(() -> validator.validateUpdate(role(2L, "Admin")))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getApiError().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getApiError().getErrors()).containsExactly("Admin already exists.");
                });
    }

    @Test
    void validateUpdateSkipsDuplicateCheckWhenNameIsUnchanged() {
        when(appRoleService.findById(2L)).thenReturn(Optional.of(role(2L, "Editor")));

        assertThatCode(() -> validator.validateUpdate(role(2L, "Editor"))).doesNotThrowAnyException();

        verify(appRoleService, never()).findByName(anyString());
    }

    @Test
    void validateUpdateRejectsMissingRole() {
        when(appRoleService.findById(2L)).thenReturn(Optional.empty());
        when(i18n.getMessage("error.dataNotfound")).thenReturn("Data not found");
        when(i18n.getMessage("error.error")).thenReturn("Error");

        assertThatThrownBy(() -> validator.validateUpdate(role(2L, "Editor")))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getApiError().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getApiError().getErrors()).containsExactly("Data not found");
                });
    }

    @Test
    void failedValidationDoesNotLeakErrorsIntoNextCall() {
        when(appRoleService.findByName("Admin")).thenReturn(Optional.of(role(1L, "Admin")));
        when(appRoleService.findByName("Auditor")).thenReturn(Optional.empty());
        when(appRoleService.findByName("Editor")).thenReturn(Optional.of(role(3L, "Editor")));
        when(i18n.getMessage("error.validateDuplicate", "Admin")).thenReturn("Admin already exists.");
        when(i18n.getMessage("error.validateDuplicate", "Editor")).thenReturn("Editor already exists.");
        when(i18n.getMessage("error.error")).thenReturn("Error");

        assertThatThrownBy(() -> validator.validateCreate(role(null, "Admin"))).isInstanceOf(ApiException.class);

        assertThatCode(() -> validator.validateCreate(role(null, "Auditor"))).doesNotThrowAnyException();
        assertThatThrownBy(() -> validator.validateCreate(role(null, "Editor")))
                .isInstanceOfSatisfying(ApiException.class, exception -> assertThat(exception.getApiError().getErrors())
                        .containsExactly("Editor already exists."));
    }

    private static AppRole role(Long id, String name) {
        AppRole role = new AppRole(name, true);
        role.setId(id);
        return role;
    }
}
