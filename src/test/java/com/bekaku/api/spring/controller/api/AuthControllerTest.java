package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.*;
import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.properties.AppDefaultsProperties;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.CookieProperties;
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

    private static final String API_CLIENT_NAME = "default";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh) Test";
    private static final Long USER_ID = 1L;
    private static final Long TARGET_USER_ID = 2L;
    private static final Long DEFAULT_ROLE_ID = 2L;

    @Mock
    private AppUserService appUserService;
    @Mock
    private AuthService authService;
    @Mock
    private AccessTokenService accessTokenService;
    @Mock
    private EncryptService encryptService;
    @Mock
    private AppRoleService appRoleService;
    @Mock
    private ApiClientService apiClientService;
    @Mock
    private JwtService jwtService;
    @Mock
    private I18n i18n;
    @Mock
    private IdentityLinkService identityLinkService;
    @Mock
    private CookieUtil cookieUtil;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private AuthController controller;
    private AppDefaultsProperties appDefaultsProperties;

    private ApiClient apiClient;
    private AppUser user;
    private RefreshTokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        appDefaultsProperties = new AppDefaultsProperties(DEFAULT_ROLE_ID, "1234", 8192L, 3L);
        AppProperties appProperties = mock(AppProperties.class);
        when(appProperties.jwt()).thenReturn(new JwtProperties(
                "test-secret", "_at", "_rt", "_cuid", 15L, 7L));
        when(appProperties.cookie()).thenReturn(new CookieProperties(false, "Lax"));

        controller = new AuthController(appUserService, authService, accessTokenService, encryptService,
                appRoleService, apiClientService, jwtService, i18n, appProperties,
                identityLinkService, cookieUtil, appDefaultsProperties);
        injectI18nIntoHierarchy(controller);

        when(i18n.getMessage(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(i18n.getMessage(anyString(), any(Object[].class))).thenAnswer(inv -> inv.getArgument(0));

        apiClient = new ApiClient(API_CLIENT_NAME, false, true);
        apiClient.setId(10L);

        user = new AppUser();
        user.setId(USER_ID);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("$2a$hashed");

        tokenResponse = RefreshTokenResponse.builder()
                .userId(USER_ID)
                .authenticationToken("access.jwt.token")
                .refreshToken("refresh-token-value")
                .expiresAt(new Date())
                .build();

        when(request.getHeader(ConstantData.X_REAL_IP)).thenReturn("127.0.0.1");
        when(cookieUtil.setCookie(any(), any(), any(), any(), anyBoolean()))
                .thenAnswer(inv -> ResponseCookie.from(
                        (String) inv.getArgument(0), (String) inv.getArgument(1)).build());
        when(cookieUtil.clearCookie(any(), any(), anyBoolean()))
                .thenAnswer(inv -> ResponseCookie.from((String) inv.getArgument(0), "").build());
    }

    private ForgotPasswordRequest forgot(String email, String token, String newPassword) {
        ForgotPasswordRequest dto = new ForgotPasswordRequest();
        ReflectionTestUtils.setField(dto, "email", email);
        ReflectionTestUtils.setField(dto, "token", token);
        if (newPassword != null) {
            ReflectionTestUtils.setField(dto, "newPassword", newPassword);
        }
        return dto;
    }

    private void injectI18nIntoHierarchy(Object target) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField("i18n");
                field.setAccessible(true);
                field.set(target, i18n);
            } catch (NoSuchFieldException ignored) {
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            type = type.getSuperclass();
        }
    }

    private LoginRequest loginRequest(String identifier, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmailOrUsername(identifier);
        loginRequest.setPassword(password);
        return loginRequest;
    }

    private void stubValidLogin() {
        when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
        when(appUserService.findActiveByEmailOrUserName("john")).thenReturn(Optional.of(user));
        when(encryptService.check("password123", user.getPassword())).thenReturn(true);
        when(authService.login(eq(user), any(LoginRequest.class), eq(apiClient), anyString(), any()))
                .thenReturn(tokenResponse);
    }

    private ResponseCookie cookie(String name, String value) {
        return ResponseCookie.from(name, value).build();
    }

    private AccessToken sessionToken(AppUser owner) {
        return new AccessToken(owner, new Date(System.currentTimeMillis() + 60_000),
                false, apiClient, null, null, null);
    }

    // ---------------------------------------------------------------- signup

    @Nested
    @DisplayName("POST /api/auth/signup")
    class Signup {

        @Test
        void assignsOnlyDefaultRoleAndEncryptsPassword() {
            UserRegisterRequest dto = new UserRegisterRequest();
            dto.setEmail("new@example.com");
            dto.setUsername("newuser");
            dto.setPassword("plainpwd");
            dto.setSelectedRoles(new Long[]{999L});
            dto.setCheckValidate(false);

            when(appUserService.findByUsername("newuser")).thenReturn(Optional.empty());
            when(appUserService.findByEmail("new@example.com")).thenReturn(Optional.empty());
            AppRole defaultRole = new AppRole("USER", true);
            when(appRoleService.findById(DEFAULT_ROLE_ID)).thenReturn(Optional.of(defaultRole));
            when(encryptService.encrypt(anyString())).thenReturn("$2a$encrypted");

            ResponseEntity<ResponseMessage> result = controller.signup(dto);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
            verify(appUserService).save(captor.capture());
            AppUser saved = captor.getValue();
            assertThat(saved.getPassword()).isEqualTo("$2a$encrypted");
            assertThat(saved.getAppRoles()).containsExactly(defaultRole);
        }

        @Test
        void rejectsDuplicateUsername() {
            UserRegisterRequest dto = new UserRegisterRequest();
            dto.setEmail("new@example.com");
            dto.setUsername("john");
            dto.setPassword("plainpwd");
            dto.setCheckValidate(false);

            when(appUserService.findByUsername("john")).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> controller.signup(dto))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getApiError().getStatus())
                    .isEqualTo(HttpStatus.BAD_REQUEST);
            verify(appUserService, never()).save(any());
        }
    }

    // ----------------------------------------------------------------- login

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        void returnsTokensAndSetsAuthCookies() {
            stubValidLogin();

            RefreshTokenResponse result = controller.login(
                    loginRequest("john", "password123"), request, response, API_CLIENT_NAME, USER_AGENT);

            assertThat(result).isSameAs(tokenResponse);
            verify(response, times(3)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        }

        @Test
        void wrongPasswordIsRejectedWithGenericError() {
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(appUserService.findActiveByEmailOrUserName("john")).thenReturn(Optional.of(user));
            when(encryptService.check("wrong", user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> controller.login(
                    loginRequest("john", "wrong"), request, response, API_CLIENT_NAME, USER_AGENT))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getApiError().getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN);
            verify(response, never()).addHeader(anyString(), anyString());
        }

        @Test
        void unknownApiClientIsRejected() {
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.login(
                    loginRequest("john", "password123"), request, response, API_CLIENT_NAME, USER_AGENT))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getApiError().getStatus())
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    // -------------------------------------------------------------- loginApi

    @Nested
    @DisplayName("POST /api/auth/loginApi")
    class LoginApi {

        @Test
        void returnsTokensWithoutCookies() {
            stubValidLogin();

            RefreshTokenResponse result = controller.loginApi(
                    loginRequest("john", "password123"), request, response, API_CLIENT_NAME, USER_AGENT);

            assertThat(result).isSameAs(tokenResponse);
            verify(response, never()).addHeader(anyString(), anyString());
        }
    }

    // -------------------------------------------------------- linkedAccounts

    @Nested
    @DisplayName("POST /api/auth/linkedAccounts")
    class LinkedAccounts {

        @Test
        void returnsLinkedAccountsOfCurrentUser() {
            AppUserDto principal = new AppUserDto();
            principal.setId(USER_ID);
            List<AppUserDto> linked = List.of(new AppUserDto());
            when(appUserService.findById(USER_ID)).thenReturn(Optional.of(user));
            when(identityLinkService.getLinkedAccounts(user)).thenReturn(linked);

            ResponseEntity<List<AppUserDto>> result = controller.getLinkedAccounts(principal);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isSameAs(linked);
        }

        @Test
        void missingUserIsForbidden() {
            AppUserDto principal = new AppUserDto();
            principal.setId(404L);
            when(appUserService.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.getLinkedAccounts(principal))
                    .isInstanceOf(ApiException.class);
        }
    }

    // ------------------------------------------------------------ linkAccount

    @Nested
    @DisplayName("POST /api/auth/linkAccount")
    class LinkAccount {

        @Test
        void linksAccountThenLogsInTarget() {
            AppUserDto principal = new AppUserDto();
            principal.setId(USER_ID);
            AppUser targetUser = new AppUser();
            targetUser.setId(TARGET_USER_ID);
            targetUser.setPassword("$2a$hashed");

            when(appUserService.findById(USER_ID)).thenReturn(Optional.of(user));
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(appUserService.findActiveByEmailOrUserName("target")).thenReturn(Optional.of(targetUser));
            when(encryptService.check("password123", targetUser.getPassword())).thenReturn(true);
            when(authService.login(eq(targetUser), eq(apiClient), anyString(), any()))
                    .thenReturn(tokenResponse);
            doNothing().when(identityLinkService).linkAccount(user, targetUser);

            ResponseEntity<RefreshTokenResponse> result = controller.linkAccount(
                    principal, loginRequest("target", "password123"),
                    request, response, API_CLIENT_NAME, USER_AGENT);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(identityLinkService).linkAccount(user, targetUser);
            verify(response, times(3)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        }
    }

    // ---------------------------------------------------------- switchAccount

    @Nested
    @DisplayName("POST /api/auth/switchAccount/{targetUserId}")
    class SwitchAccount {

        @Test
        void slowPathLogsIntoTargetWhenNoRefreshCookieExists() {
            AppUserDto principal = new AppUserDto();
            principal.setId(USER_ID);
            when(request.getCookies()).thenReturn(null);
            AppUser targetUser = new AppUser();
            targetUser.setId(TARGET_USER_ID);
            when(appUserService.findById(TARGET_USER_ID)).thenReturn(Optional.of(targetUser));
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(authService.login(eq(targetUser), eq(apiClient), anyString(), any()))
                    .thenReturn(tokenResponse);

            ResponseEntity<?> result = controller.switchAccount(
                    principal, TARGET_USER_ID, request, response, API_CLIENT_NAME, USER_AGENT);

            verify(identityLinkService).validateSwitchAccount(USER_ID, TARGET_USER_ID);
            verify(authService).login(eq(targetUser), eq(apiClient), anyString(), any());
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void fastPathSwitchesOnValidTargetRefreshCookie() {
            AppUserDto principal = new AppUserDto();
            principal.setId(USER_ID);
            Cookie refreshCookie = new Cookie("_rt" + TARGET_USER_ID, "target-refresh-token");
            when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
            when(accessTokenService.findByToken("target-refresh-token"))
                    .thenReturn(Optional.of(sessionToken(user)));
            when(accessTokenService.isTokenExpired(any(AccessToken.class))).thenReturn(false);

            ResponseEntity<?> result = controller.switchAccount(
                    principal, TARGET_USER_ID, request, response, API_CLIENT_NAME, USER_AGENT);

            verify(identityLinkService).validateSwitchAccount(USER_ID, TARGET_USER_ID);
            verify(authService, never()).login(any(), any(), anyString(), any());
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), contains("_cuid=" + TARGET_USER_ID));
        }

        @Test
        void rejectsSwitchWithoutLinkageEvenWithValidTargetCookie() {
            AppUserDto principal = new AppUserDto();
            principal.setId(USER_ID);
            Cookie refreshCookie = new Cookie("_rt" + TARGET_USER_ID, "target-refresh-token");
            when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
            when(accessTokenService.findByToken("target-refresh-token"))
                    .thenReturn(Optional.of(sessionToken(user)));
            when(accessTokenService.isTokenExpired(any(AccessToken.class))).thenReturn(false);
            doThrow(new ApiException(null))
                    .when(identityLinkService).validateSwitchAccount(USER_ID, TARGET_USER_ID);

            assertThatThrownBy(() -> controller.switchAccount(
                    principal, TARGET_USER_ID, request, response, API_CLIENT_NAME, USER_AGENT))
                    .isInstanceOf(ApiException.class);
            verify(authService, never()).login(any(), any(), anyString(), any());
            verify(response, never()).addHeader(anyString(), anyString());
        }
    }

    // ------------------------------------------------------ removeLinkAccount

    @Nested
    @DisplayName("POST /api/auth/removeLinkAccount/{targetUserId}")
    class RemoveLinkAccount {

        @Test
        void removesLinkAndClearsCookies() {
            AppUserDto principal = new AppUserDto();
            principal.setId(USER_ID);
            when(request.getCookies()).thenReturn(null);

            ResponseEntity<?> result = controller.removeLinkAccount(
                    principal, TARGET_USER_ID, request, response);

            verify(identityLinkService).removeLinkAccount(USER_ID, TARGET_USER_ID);
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    // ------------------------------------------------------------ refreshToken

    @Nested
    @DisplayName("POST /api/auth/refreshToken")
    class RefreshToken {

        private HttpServletRequest requestWithCookies(Cookie... cookies) {
            HttpServletRequest req = mock(HttpServletRequest.class);
            when(req.getCookies()).thenReturn(cookies);
            when(req.getHeader(ConstantData.X_REAL_IP)).thenReturn("127.0.0.1");
            return req;
        }

        @Test
        void missingCurrentUserCookieIsForbidden() {
            HttpServletRequest req = requestWithCookies();
            when(req.getHeader(ConstantData.X_REAL_IP)).thenReturn("127.0.0.1");

            assertThatThrownBy(() -> controller.refreshToken(req, response, API_CLIENT_NAME, USER_AGENT))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getApiError().getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        void rotatesTokensOnHappyPath() {
            HttpServletRequest req = requestWithCookies(
                    new Cookie("_cuid", USER_ID.toString()),
                    new Cookie("_rt" + USER_ID, "valid-refresh-token"));
            AccessToken accessToken = sessionToken(user);
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(accessTokenService.findByTokenAndRevoked("valid-refresh-token", false))
                    .thenReturn(Optional.of(accessToken));
            when(accessTokenService.isTokenExpired(accessToken)).thenReturn(false);
            when(authService.refreshToken(eq(accessToken), eq(apiClient), anyString()))
                    .thenReturn(tokenResponse);

            RefreshTokenResponse result = controller.refreshToken(req, response, API_CLIENT_NAME, USER_AGENT);

            assertThat(result).isSameAs(tokenResponse);
            verify(accessTokenService).handleRefreshTokenReuse("valid-refresh-token");
            verify(response, times(3)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        }

        @Test
        void revokedOrUnknownTokenIsForbidden() {
            HttpServletRequest req = requestWithCookies(
                    new Cookie("_cuid", USER_ID.toString()),
                    new Cookie("_rt" + USER_ID, "revoked-token"));
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(accessTokenService.findByTokenAndRevoked("revoked-token", false))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.refreshToken(req, response, API_CLIENT_NAME, USER_AGENT))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getApiError().getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN);
            verify(authService, never()).refreshToken(any(), any(), anyString());
        }
    }

    // --------------------------------------------------------- refreshTokenApi

    @Nested
    @DisplayName("POST /api/auth/refreshTokenApi")
    class RefreshTokenApi {

        @Test
        void rotatesTokensOnHappyPath() {
            RefreshTokenRequest dto = new RefreshTokenRequest("body-refresh-token", null, null, false);
            AccessToken accessToken = sessionToken(user);
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(accessTokenService.findByTokenAndRevoked("body-refresh-token", false))
                    .thenReturn(Optional.of(accessToken));
            when(accessTokenService.isTokenExpired(accessToken)).thenReturn(false);
            when(authService.refreshToken(eq(accessToken), eq(apiClient), anyString()))
                    .thenReturn(tokenResponse);

            RefreshTokenResponse result = controller.refreshTokenApi(dto, API_CLIENT_NAME, USER_AGENT, USER_ID);

            assertThat(result).isSameAs(tokenResponse);
            verify(accessTokenService).handleRefreshTokenReuse("body-refresh-token");
        }

        @Test
        void unknownOrRevokedTokenIsForbiddenAfterReuseDetection() {
            RefreshTokenRequest dto = new RefreshTokenRequest("stolen-token", null, null, false);
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(accessTokenService.findByTokenAndRevoked("stolen-token", false))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.refreshTokenApi(dto, API_CLIENT_NAME, USER_AGENT, USER_ID))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getApiError().getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN);
            verify(accessTokenService).handleRefreshTokenReuse("stolen-token");
        }
    }

    // ------------------------------------------- requestVerifyCodeToResetPwd

    @Nested
    @DisplayName("POST /api/auth/requestVerifyCodeToResetPwd")
    class RequestVerifyCode {

        @Test
        void generatesTokenForExistingEmail() throws Exception {
            ForgotPasswordRequest dto = forgot("otp-existing@example.com", null, null);
            when(appUserService.findByEmail("otp-existing@example.com")).thenReturn(Optional.of(user));
            when(accessTokenService.generateTokenBy(eq(user), any(), anyString(),
                    eq(AccessTokenServiceType.FORGOT_PASSWORD)))
                    .thenReturn(sessionToken(user));

            ResponseEntity<Object> result = controller.requestVerifyCodeToResetPwd(
                    dto, API_CLIENT_NAME, USER_AGENT);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accessTokenService).generateTokenBy(eq(user), any(), anyString(),
                    eq(AccessTokenServiceType.FORGOT_PASSWORD));
        }

        @Test
        void unknownEmailGetsIdenticalSuccessResponseWithoutTokenGeneration() throws Exception {
            ForgotPasswordRequest dto = forgot("ghost@example.com", null, null);
            when(appUserService.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

            ResponseEntity<Object> result = controller.requestVerifyCodeToResetPwd(
                    dto, API_CLIENT_NAME, USER_AGENT);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accessTokenService, never()).generateTokenBy(any(), any(), anyString(), any());
        }

        @Test
        void secondRequestWithinWindowIsRateLimited() throws Exception {
            ForgotPasswordRequest dto = forgot("ratelimit@example.com", null, null);
            when(appUserService.findByEmail("ratelimit@example.com")).thenReturn(Optional.of(user));
            when(accessTokenService.generateTokenBy(eq(user), any(), anyString(),
                    eq(AccessTokenServiceType.FORGOT_PASSWORD)))
                    .thenReturn(sessionToken(user));

            ResponseEntity<Object> first = controller.requestVerifyCodeToResetPwd(dto, API_CLIENT_NAME, USER_AGENT);
            assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);

            assertThatThrownBy(() -> controller.requestVerifyCodeToResetPwd(dto, API_CLIENT_NAME, USER_AGENT))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getApiError().getStatus())
                    .isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    // --------------------------------------------- sendVerifyCodeToResetPwd

    @Nested
    @DisplayName("POST /api/auth/sendVerifyCodeToResetPwd")
    class SendVerifyCode {

        @Test
        void wrongCodeIsBadRequest() {
            ForgotPasswordRequest dto = forgot("john@example.com", "000000", null);
            when(appUserService.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(accessTokenService.findAccessTokenByTokenAndUser(user, "000000"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.sendVerifyCodeToResetPwd(dto, API_CLIENT_NAME, USER_AGENT))
                    .isInstanceOf(ApiException.class)
                    .extracting(e -> ((ApiException) e).getApiError().getStatus())
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        void validCodeReturnsOk() {
            ForgotPasswordRequest dto = forgot("john@example.com", "123456", null);
            AccessToken token = sessionToken(user);
            when(appUserService.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(accessTokenService.findAccessTokenByTokenAndUser(user, "123456"))
                    .thenReturn(Optional.of(token));
            when(accessTokenService.isTokenExpired(token)).thenReturn(false);

            ResponseEntity<Object> result = controller.sendVerifyCodeToResetPwd(dto, API_CLIENT_NAME, USER_AGENT);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    // ------------------------------------------------------------ resetPassword

    @Nested
    @DisplayName("POST /api/auth/resetPassword")
    class ResetPassword {

        @Test
        void weakPasswordReturns400() {
            ForgotPasswordRequest dto = forgot("john@example.com", "123456", "weak");

            ResponseEntity<Object> result = controller.resetPassword(dto, API_CLIENT_NAME, USER_AGENT);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            verify(appUserService, never()).updatePasswordBy(any(), anyString());
        }

        @Test
        void strongPasswordResetsAndDeletesToken() {
            ForgotPasswordRequest dto = forgot("john@example.com", "123456", "Str0ng!Pass");
            AccessToken token = sessionToken(user);
            when(appUserService.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(accessTokenService.findAccessTokenByTokenAndUser(user, "123456"))
                    .thenReturn(Optional.of(token));
            when(accessTokenService.isTokenExpired(token)).thenReturn(false);
            when(encryptService.encrypt("Str0ng!Pass")).thenReturn("$2a$newhash");

            ResponseEntity<Object> result = controller.resetPassword(dto, API_CLIENT_NAME, USER_AGENT);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(appUserService).updatePasswordBy(user, "$2a$newhash");
            verify(accessTokenService).delete(token);
        }

        @Test
        void wrongCodeIsRejectedBeforeReset() {
            ForgotPasswordRequest dto = forgot("john@example.com", "999999", "Str0ng!Pass");
            when(appUserService.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(accessTokenService.findAccessTokenByTokenAndUser(user, "999999"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.resetPassword(dto, API_CLIENT_NAME, USER_AGENT))
                    .isInstanceOf(ApiException.class);
            verify(appUserService, never()).updatePasswordBy(any(), anyString());
        }
    }

    // ---------------------------------------------------------------- logout

    @Nested
    @DisplayName("POST /api/auth/logout")
    class Logout {

        @Test
        void logsOutSessionAndClearsCookies() {
            HttpServletRequest req = mock(HttpServletRequest.class);
            when(req.getCookies()).thenReturn(new Cookie[]{
                    new Cookie("_cuid", USER_ID.toString()),
                    new Cookie("_rt" + USER_ID, "refresh-token-value")});
            when(req.getHeader(ConstantData.X_REAL_IP)).thenReturn("127.0.0.1");
            AccessToken token = sessionToken(user);
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(accessTokenService.findByToken("refresh-token-value")).thenReturn(Optional.of(token));

            ResponseEntity<ResponseMessage> result = controller.logout(response, req, API_CLIENT_NAME);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accessTokenService).logoutProcess(token);
            verify(response, atLeastOnce()).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        }

        @Test
        void withoutCookiesStillReturnsOk() {
            HttpServletRequest req = mock(HttpServletRequest.class);
            when(req.getCookies()).thenReturn(null);
            when(req.getHeader(ConstantData.X_REAL_IP)).thenReturn("127.0.0.1");

            ResponseEntity<ResponseMessage> result = controller.logout(response, req, API_CLIENT_NAME);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accessTokenService, never()).logoutProcess(any());
        }
    }

    // -------------------------------------------------------------- logoutApi

    @Nested
    @DisplayName("POST /api/auth/logoutApi")
    class LogoutApi {

        @Test
        void deletesSessionForProvidedRefreshToken() {
            RefreshTokenRequest dto = new RefreshTokenRequest("api-refresh-token", null, null, false);
            AccessToken token = sessionToken(user);
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));
            when(accessTokenService.findByToken("api-refresh-token")).thenReturn(Optional.of(token));

            ResponseEntity<ResponseMessage> result = controller.logoutApi(dto, API_CLIENT_NAME, USER_ID);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accessTokenService).logoutProcess(token);
        }

        @Test
        void emptyRefreshTokenStillReturnsOk() {
            RefreshTokenRequest dto = new RefreshTokenRequest("", null, null, false);
            when(apiClientService.findByApiName(API_CLIENT_NAME)).thenReturn(Optional.of(apiClient));

            ResponseEntity<ResponseMessage> result = controller.logoutApi(dto, API_CLIENT_NAME, USER_ID);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accessTokenService, never()).logoutProcess(any());
        }
    }

    // ---------------------------------------------- removeAccessTokenSession

    @Nested
    @DisplayName("DELETE /api/auth/removeAccessTokenSession")
    class RemoveAccessTokenSession {

        @Test
        void ownerCanRemoveOwnSession() {
            AppUserDto principal = new AppUserDto();
            principal.setId(USER_ID);
            AccessToken token = sessionToken(user);
            when(accessTokenService.findById(55L)).thenReturn(Optional.of(token));

            ResponseEntity<Object> result = controller.removeAccessTokenSession(principal, 55L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accessTokenService).logoutProcess(token);
        }

        @Test
        void doesNotRemoveSessionOwnedByAnotherUser() {
            AppUserDto principal = new AppUserDto();
            principal.setId(USER_ID);
            AppUser otherUser = new AppUser();
            otherUser.setId(99L);
            AccessToken foreignToken = sessionToken(otherUser);
            when(accessTokenService.findById(66L)).thenReturn(Optional.of(foreignToken));

            ResponseEntity<Object> result = controller.removeAccessTokenSession(principal, 66L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(accessTokenService, never()).logoutProcess(foreignToken);
        }
    }
}
