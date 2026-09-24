package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    AppUserService appUserService;
    @Mock
    I18n i18n;

    private UserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserValidator(appUserService, i18n);
    }

    @Test
    void validateCreateRejectsDuplicateEmailAndUsernameWithBadRequest() {
        when(appUserService.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(user(1L, "someone", "taken@example.com")));
        when(appUserService.findByUsername("alice")).thenReturn(Optional.of(user(2L, "alice", "alice@example.com")));
        when(i18n.getMessage("error.validateDuplicateEmail", "taken@example.com"))
                .thenReturn("Email taken@example.com already exists.");
        when(i18n.getMessage("error.validateDuplicateUsername", "alice")).thenReturn("Username alice already exists.");
        when(i18n.getMessage("error.error")).thenReturn("Error");

        assertThatThrownBy(() -> validator.validateCreate(user(null, "alice", "taken@example.com")))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getApiError().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getApiError().getMessage()).isEqualTo("Error");
                    assertThat(exception.getApiError().getErrors()).containsExactly(
                            "Email taken@example.com already exists.",
                            "Username alice already exists.");
                });
    }

    @Test
    void validateUpdateRejectsEmailOfAnotherUser() {
        when(appUserService.findById(5L)).thenReturn(Optional.of(user(5L, "bob", "bob@example.com")));
        when(appUserService.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(user(1L, "someone", "taken@example.com")));
        when(i18n.getMessage("error.validateDuplicateEmail", "taken@example.com"))
                .thenReturn("Email taken@example.com already exists.");
        when(i18n.getMessage("error.error")).thenReturn("Error");

        assertThatThrownBy(() -> validator.validateUpdate(user(5L, "bob", "taken@example.com")))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getApiError().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getApiError().getErrors())
                            .containsExactly("Email taken@example.com already exists.");
                });
        verify(appUserService, never()).findByUsername(anyString());
    }

    @Test
    void errorsCollectedBeforeAnUnexpectedFailureDoNotLeakIntoNextCall() {
        when(appUserService.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(user(1L, "someone", "taken@example.com")));
        when(i18n.getMessage("error.validateDuplicateEmail", "taken@example.com"))
                .thenReturn("Email taken@example.com already exists.");
        when(appUserService.findByUsername("alice")).thenThrow(new IllegalStateException("database unavailable"));
        when(appUserService.findByEmail("bob@example.com")).thenReturn(Optional.empty());
        when(appUserService.findByUsername("bob")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validator.validateCreate(user(null, "alice", "taken@example.com")))
                .isInstanceOf(IllegalStateException.class);

        assertThatCode(() -> validator.validateCreate(user(null, "bob", "bob@example.com")))
                .doesNotThrowAnyException();
    }

    @Test
    void concurrentValidationsDoNotShareErrors() throws Exception {
        CountDownLatch aliceRecordedError = new CountDownLatch(1);
        CountDownLatch bobFinished = new CountDownLatch(1);
        when(appUserService.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(user(1L, "someone", "taken@example.com")));
        when(i18n.getMessage("error.validateDuplicateEmail", "taken@example.com"))
                .thenReturn("Email taken@example.com already exists.");
        when(i18n.getMessage("error.error")).thenReturn("Error");
        // Alice's call pauses after recording her duplicate-email error until Bob's call has finished.
        when(appUserService.findByUsername("alice")).thenAnswer(invocation -> {
            aliceRecordedError.countDown();
            bobFinished.await(5, SECONDS);
            return Optional.empty();
        });
        when(appUserService.findByEmail("bob@example.com")).thenReturn(Optional.empty());
        when(appUserService.findByUsername("bob")).thenReturn(Optional.empty());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> alice = executor.submit(() -> validator.validateCreate(user(null, "alice", "taken@example.com")));
            assertThat(aliceRecordedError.await(5, SECONDS)).isTrue();
            try {
                assertThatCode(() -> validator.validateCreate(user(null, "bob", "bob@example.com")))
                        .doesNotThrowAnyException();
            } finally {
                bobFinished.countDown();
            }

            assertThatThrownBy(() -> alice.get(5, SECONDS))
                    .isInstanceOf(ExecutionException.class)
                    .cause()
                    .isInstanceOfSatisfying(ApiException.class, exception -> assertThat(exception.getApiError().getErrors())
                            .containsExactly("Email taken@example.com already exists."));
        } finally {
            executor.shutdownNow();
        }
    }

    private static AppUser user(Long id, String username, String email) {
        AppUser user = new AppUser();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
