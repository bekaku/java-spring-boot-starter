package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.ai.AiFaceRegconitionServiceClient;
import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.FaceRecognitionDtos;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.exception.BaseResponseException;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AppUserFaceService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.FileManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaceRecognitionServiceOwnershipTest {

    private static final long USER_ID = 10L;
    private static final long OTHER_USER_ID = 11L;
    private static final long FILE_ID = 20L;

    @Mock
    AppProperties appProperties;
    @Mock
    AiFaceRegconitionServiceClient faceClient;
    @Mock
    AppUserFaceService appUserFaceService;
    @Mock
    AppUserService appUserService;
    @Mock
    FileManagerService fileManagerService;
    @Mock
    I18n i18n;

    private FaceRecognitionServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        service = new FaceRecognitionServiceImpl(
                appProperties, faceClient, appUserFaceService, appUserService, fileManagerService);
        injectExceptionI18n(service);
        when(i18n.getMessage("error.403")).thenReturn("Forbidden");
    }

    @Test
    void rejectsRegisteringFaceForAnotherUserBeforeAccessingData() {
        FaceRecognitionDtos.RegisterRequest request =
                new FaceRecognitionDtos.RegisterRequest(OTHER_USER_ID, FILE_ID);

        assertForbidden(() -> service.registerhFace(USER_ID, request));

        verifyNoInteractions(appUserService, fileManagerService, appUserFaceService, faceClient);
    }

    @Test
    void rejectsFileOwnedByAnotherUserBeforeReplacingExistingFace() {
        AppUser currentUser = user(USER_ID);
        FileManager otherUsersFile = new FileManager();
        otherUsersFile.setOwner(user(OTHER_USER_ID));
        when(appUserService.findById(USER_ID)).thenReturn(Optional.of(currentUser));
        when(fileManagerService.findById(FILE_ID)).thenReturn(Optional.of(otherUsersFile));

        FaceRecognitionDtos.RegisterRequest request =
                new FaceRecognitionDtos.RegisterRequest(USER_ID, FILE_ID);

        assertForbidden(() -> service.registerhFace(USER_ID, request));

        verifyNoInteractions(appUserFaceService, faceClient);
    }

    private void assertForbidden(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOfSatisfying(ApiException.class,
                        exception -> assertThat(exception.getApiError().getStatus())
                                .isEqualTo(HttpStatus.FORBIDDEN));
    }

    private static AppUser user(long id) {
        AppUser user = new AppUser();
        user.setId(id);
        return user;
    }

    private void injectExceptionI18n(BaseResponseException target) throws Exception {
        Field field = BaseResponseException.class.getDeclaredField("i18n");
        field.setAccessible(true);
        field.set(target, i18n);
    }
}
