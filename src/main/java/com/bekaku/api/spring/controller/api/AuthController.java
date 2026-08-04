package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.*;
import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.bekaku.api.spring.util.ConstantData.UNDER_SCORE;


@Slf4j
@RequestMapping(path = "/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController extends BaseApiController {

    private final AppUserService appUserService;
    private final AuthService authService;
    private final AccessTokenService accessTokenService;
    private final EncryptService encryptService;
    private final AppRoleService appRoleService;
    private final ApiClientService apiClientService;
    private final JwtService jwtService;
    private final I18n i18n;
    private final AppProperties appProperties;

    @Value("${app.defaults.image}")
    String defaultImage;

    @Value("${app.defaults.role}")
    Long defaultRole;

    @Value("${environments.production}")
    boolean isProduction;

    @Value("${app.domain}")
    String appDomain;
    private final JwtProperties jwtProperties;

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@Valid @RequestBody UserRegisterRequest registerDto) {
        validateUserRegister(registerDto);

        //user can have many role
        Set<AppRole> appRoles = new HashSet<>();
        if (registerDto.getSelectedRoles().length > 0) {
            Optional<AppRole> role;
            for (long roleId : registerDto.getSelectedRoles()) {
                role = appRoleService.findById(roleId);
                role.ifPresent(appRoles::add);
            }
        }
//        else {
        //save defult role for new user
//            Optional<Role> role = roleService.findById(defaultRole);
//            role.ifPresent(roles::add);
//        }
        AppUser appUser = new AppUser();
        appUser.addNew(
                registerDto.getUsername(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                registerDto.isActive()
        );
        appUser.setAppRoles(appRoles);
        //encrypt pwd
        appUser.setPassword(encryptService.encrypt(appUser.getPassword(), appUser.getSalt()));
        appUserService.save(appUser);
        return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK, i18n.getMessage("success.logoutSuccess")), HttpStatus.OK);
    }

    private void validateUserRegister(@RequestBody UserRegisterRequest registerParam) {

        List<String> errors = new ArrayList<>();
        if (appUserService.findByUsername(registerParam.getUsername()).isPresent()) {
            errors.add(i18n.getMessage("error.validateDuplicateUsername", registerParam.getUsername()));
        }
        if (appUserService.findByEmail(registerParam.getEmail()).isPresent()) {
            errors.add(i18n.getMessage("error.validateDuplicateEmail", registerParam.getEmail()));
        }
        if (!errors.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.OK, i18n.getMessage("error.error"), errors));
        }
    }

    @PostMapping("/login")
    public RefreshTokenResponse login(@Valid @RequestBody LoginRequest loginRequest,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                      @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {

        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);

        if (apiClient.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.OK, i18n.getMessage("error.error"),
                    i18n.getMessage("error.apiClientNotFound")));
        }

        AppUser user = validateLogin(loginRequest);
        RefreshTokenResponse tokenResponse = authService.login(user, loginRequest, apiClient.get(), userAgent, AppUtil.getIpaddress(request));
        setRefreshTokenCookie(response, tokenResponse);
        return tokenResponse;
//        return authService.login(user.get(), loginRequest, apiClient.get(), userAgent, AppUtil.getIpaddress(request));
    }

    @PostMapping("/loginApi")
    public RefreshTokenResponse loginApi(@Valid @RequestBody LoginRequest loginRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                         @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {

        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);

        if (apiClient.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.OK, i18n.getMessage("error.error"),
                    i18n.getMessage("error.apiClientNotFound")));
        }

        AppUser user = validateLogin(loginRequest);
        return authService.login(user, loginRequest, apiClient.get(), userAgent, AppUtil.getIpaddress(request));
    }

    private AppUser validateLogin(LoginRequest loginRequest) {

        if (loginRequest.getEmailOrUsername() == null) {
            throw new ApiException(new ApiError(HttpStatus.OK, i18n.getMessage("error.error"),
                    i18n.getMessage("error.apiClientNotFound")));
        }
        Optional<AppUser> user = appUserService.findActiveByEmailOrUserName(loginRequest.getEmailOrUsername());
        if (user.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.OK, i18n.getMessage("error.error"),
                    i18n.getMessage("error.userNotFound", loginRequest.getEmailOrUsername())));
        }
        if (!encryptService.check(loginRequest.getPassword(), user.get().getPassword()) || !user.get().isActive()) {
            throw new ApiException(new ApiError(HttpStatus.OK, i18n.getMessage("error.error"),
                    i18n.getMessage("error.loginWrong")));
        }

        return user.get();
    }


    private String getRefreshKeyBy(Long currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        return jwtProperties.refreshTokenName() + UNDER_SCORE + currentUserId;
    }

    private String getCurrentUserKeyBy() {
        return jwtProperties.currentUserKey();
    }

    private String getJwtKeyBy(Long currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        return jwtProperties.tokenName() + UNDER_SCORE + currentUserId;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, RefreshTokenResponse tokenResponse) {
        setCookieByName(response, getJwtKeyBy(tokenResponse.getUserId()), tokenResponse.getAuthenticationToken(), jwtService.expireJwtSecond(), true);
        setCookieByName(response, getRefreshKeyBy(tokenResponse.getUserId()), tokenResponse.getRefreshTokenKey(), jwtService.expireRefreshSecond(), true);
        setCookieByName(response, getCurrentUserKeyBy(), tokenResponse.getUserId().toString(), jwtService.expireRefreshSecond(), false);
    }

    private void setCookieByName(HttpServletResponse response, String cookieName, String value, long maxAgeSeconds, boolean httponly) {
        ResponseCookie jwtCookie = ResponseCookie.from(cookieName, value)
                .httpOnly(httponly)
                .secure(appProperties.cookie().secure()) // true in prod (HTTPS), false in dev
                .path("/")
                .sameSite(appProperties.cookie().sameSite()) // "Lax" for dev; "None" + secure for prod
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
    }

    private void deleteCookie(HttpServletRequest request, HttpServletResponse response, Long currentUserId) {
        if (currentUserId != null) {
            deleteCookieByName(response, getJwtKeyBy(currentUserId), true);
            deleteCookieByName(response, getRefreshKeyBy(currentUserId), true);
            deleteCookieByName(response, getCurrentUserKeyBy(), false);
            setCurrentUserToAnother(request, response, currentUserId);
        }
    }

    private void deleteCookieByName(HttpServletResponse response, String cookieName, boolean httponly) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(httponly)
                .secure(appProperties.cookie().secure())
                .path("/")
                .maxAge(0) // Deletes cookie
                .sameSite(appProperties.cookie().sameSite()) // Must match how it was originally set
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void setCurrentUserToAnother(HttpServletRequest request, HttpServletResponse response, Long loggedOutUserId) {
        if (request.getCookies() == null) return;

        String refreshCookiePrefix = jwtProperties.refreshTokenName() + "_";
        boolean isCurrentUserSet = false;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().startsWith(refreshCookiePrefix)) {

                String otherUserIdStr = cookie.getName().substring(refreshCookiePrefix.length());

                if (otherUserIdStr.equals(loggedOutUserId.toString())) {
                    continue;
                }

                Long otherUserId;
                try {
                    otherUserId = Long.valueOf(otherUserIdStr);
                } catch (NumberFormatException e) {
                    deleteCookieByName(response, cookie.getName(), true);
                    continue;
                }

                String refreshTokenValue = cookie.getValue();
                Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(refreshTokenValue, false);

                if (accessToken.isPresent() && !accessTokenService.isTokenExpired(accessToken.get())) {
                    if (!isCurrentUserSet) {
                        setCookieByName(response, getCurrentUserKeyBy(), otherUserIdStr, jwtService.expireRefreshSecond(), false);
                        isCurrentUserSet = true;
                    }
                } else {
                    deleteCookieByName(response, getJwtKeyBy(otherUserId), true);
                    deleteCookieByName(response, getRefreshKeyBy(otherUserId), true);
                    accessToken.ifPresent(accessTokenService::delete);
                }
            }
        }
    }

    @PostMapping("/refreshToken")
    public RefreshTokenResponse refreshToken(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                             @RequestHeader(value = ConstantData.USER_AGENT) String userAgent,
                                             @RequestHeader(value = ConstantData.X_USER_ID, required = false, defaultValue = "0") Long currentUserId) {

        String refreshTokenKey = AppUtil.getCookieByName(request.getCookies(), getRefreshKeyBy(currentUserId));
        log.info("currentUserId: {}, refreshTokenCookie: {}", currentUserId, refreshTokenKey);
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            deleteCookie(request, response, currentUserId);
            throwUnauthorizes();
        }
        if (AppUtil.isEmpty(refreshTokenKey)) {
            deleteCookie(request, response, currentUserId);
            throwUnauthorizes();
        }
        Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(refreshTokenKey, false);
        if (accessToken.isEmpty()) {
            deleteCookie(request, response, currentUserId);
            throwUnauthorizes();
        }

        //validate expred token
        boolean isExpired = accessTokenService.isTokenExpired(accessToken.get());
        if (isExpired) {
            deleteCookie(request, response, currentUserId);
            throwUnauthorizes();
        }
        RefreshTokenResponse tokenResponse = authService.refreshToken(accessToken.get(), apiClient.get(), AppUtil.getUserAgent(userAgent));
        setRefreshTokenCookie(response, tokenResponse);
        return tokenResponse;
    }

    @PostMapping("/refreshTokenApi")
    public RefreshTokenResponse refreshTokenApi(@Valid @RequestBody RefreshTokenRequest dto,
                                                @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                @RequestHeader(value = ConstantData.USER_AGENT) String userAgent,
                                                @RequestHeader(value = ConstantData.X_USER_ID, required = false, defaultValue = "0") Long currentUserId) {

        String refreshTokenKey = dto.getRefreshToken();
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            throwUnauthorizes();
        }
        if (AppUtil.isEmpty(refreshTokenKey)) {
            throwUnauthorizes();
        }
        log.info("dto.getRefreshToken() :{}", refreshTokenKey);
        Optional<String> tokenKey = jwtService.getSubFromToken(refreshTokenKey, apiClient.get());
        if (tokenKey.isEmpty()) {
            throwUnauthorizes();
        }
        Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(tokenKey.get(), false);
        if (accessToken.isEmpty()) {
            throwUnauthorizes();
        }

        //validate expred token
        boolean isExpired = accessTokenService.isTokenExpired(accessToken.get());
        if (isExpired) {
            throwUnauthorizes();
        }
        return authService.refreshToken(accessToken.get(), apiClient.get(), AppUtil.getUserAgent(userAgent));
    }

    private void throwUnauthorizes() {
        throw new ApiException(new ApiError(HttpStatus.FORBIDDEN, i18n.getMessage("error.error"), "Session Expired"));
    }

    @PostMapping("/requestVerifyCodeToResetPwd")
    public ResponseEntity<Object> requestVerifyCodeToResetPwd(@Valid @RequestBody ForgotPasswordRequest reqBody,
                                                              @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                              @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) throws MessagingException {

        Optional<AppUser> user = appUserService.findByEmail(reqBody.getEmail());
        if (user.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, i18n.getMessage("error.error"),
                    i18n.getMessage("error.userNotFound", reqBody.getEmail())));
        }

        String token = AppUtil.generateRandomNumber(6);
        AccessToken accessToken = accessTokenService.generateTokenBy(user.get(), accessTokenService.getExpireDateBy(AccessTokenServiceType.FORGOT_PASSWORD), token, AccessTokenServiceType.FORGOT_PASSWORD);
        if (accessToken.isNewToken()) {
            //TODO
//            emailService.sendEmailRecoveryToken(accessToken);
        }
        return this.responseServerMessage(i18n.getMessage("authen.token_not_expire", reqBody.getEmail()));
    }

    @PostMapping("/sendVerifyCodeToResetPwd")
    public ResponseEntity<Object> sendVerifyCodeToResetPwd(@Valid @RequestBody ForgotPasswordRequest reqBody,
                                                           @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                           @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {
        AccessToken accessToken = getRequestForgotPasswordAccesstoken(reqBody);
        return this.responseEntity(HttpStatus.OK);
    }

    private AccessToken getRequestForgotPasswordAccesstoken(ForgotPasswordRequest reqBody) {
        Optional<AppUser> user = appUserService.findByEmail(reqBody.getEmail());
        if (user.isEmpty()) {
            throw this.responseErrorBadRequest();
        }
        if (AppUtil.isEmpty(reqBody.getToken())) {
            throw this.responseErrorBadRequest();
        }
        Optional<AccessToken> accessToken = accessTokenService.findAccessTokenByTokenAndUser(user.get(), reqBody.getToken());
        if (accessToken.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"),
                    i18n.getMessage("error.verify.code.wrong")));
        }
        boolean isExpired = accessTokenService.isTokenExpired(accessToken.get());
        if (isExpired) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"),
                    i18n.getMessage("error.token.expired")));
        }
        return accessToken.get();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody ForgotPasswordRequest reqBody,
                                                @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {
        if (AppUtil.isEmpty(reqBody.getNewPassword())) {
            throw this.responseErrorBadRequest();
        }
        //validate pwd strong
        boolean isStrong = AppUtil.validatePasswordStrong(reqBody.getNewPassword());
        if (!isStrong) {
            return this.responseServerMessage(i18n.getMessage("error.pwd.policy.alert", reqBody.getEmail()), HttpStatus.BAD_REQUEST);
        }

        AccessToken accessToken = getRequestForgotPasswordAccesstoken(reqBody);
        String newPassword = encryptService.encrypt(reqBody.getNewPassword(), accessToken.getAppUser().getSalt());
        appUserService.updatePasswordBy(accessToken.getAppUser(), newPassword);
        accessTokenService.delete(accessToken);
        return this.responseServerMessage(i18n.getMessage("helper.reset_pwd_ok", reqBody.getEmail()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(HttpServletResponse response,
                                                  HttpServletRequest request,
                                                  @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                  @RequestHeader(value = ConstantData.X_USER_ID, required = false, defaultValue = "0") Long currentUserId) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        String refreshTokenKey = AppUtil.getCookieByName(request.getCookies(), getRefreshKeyBy(currentUserId));
        if (apiClient.isPresent() && !AppUtil.isEmpty(refreshTokenKey)) {
            Optional<String> tokenKey = jwtService.getSubFromToken(refreshTokenKey, apiClient.get());
            if (tokenKey.isPresent()) {
                Optional<AccessToken> accessToken = accessTokenService.findByToken(tokenKey.get());
                accessToken.ifPresent(this::logoutProcess);
            }
        }
        deleteCookie(request, response, currentUserId);
        return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK, i18n.getMessage("success.logoutSuccess")), HttpStatus.OK);
    }

    @PostMapping("/logoutApi")
    public ResponseEntity<ResponseMessage> logoutApi(HttpServletResponse response,
                                                     HttpServletRequest request,
                                                     @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                                     @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                     @RequestHeader(value = ConstantData.X_USER_ID, required = false, defaultValue = "0") Long currentUserId) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        String refreshTokenKey = refreshTokenRequest.getRefreshToken();
        if (apiClient.isPresent() && !AppUtil.isEmpty(refreshTokenKey)) {
            Optional<String> tokenKey = jwtService.getSubFromToken(refreshTokenKey, apiClient.get());
            if (tokenKey.isPresent()) {
                Optional<AccessToken> accessToken = accessTokenService.findByToken(tokenKey.get());
                accessToken.ifPresent(this::logoutProcess);
            }
        }
        return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK, i18n.getMessage("success.logoutSuccess")), HttpStatus.OK);
    }

    private void logoutProcess(AccessToken token) {
        accessTokenService.logoutProcess(token);
    }

    @Deprecated
    @DeleteMapping("/removeAccessTokenSession")
    public ResponseEntity<Object> removeAccessTokenSession(@AuthenticationPrincipal AppUserDto userAuthen, @RequestParam(value = "id") Long id
    ) {
        Optional<AccessToken> accessToken = accessTokenService.findById(id);
        if (accessToken.isPresent() && Objects.equals(accessToken.get().getAppUser().getId(), userAuthen.getId())) {
            logoutProcess(accessToken.get());
        }
        return this.responseServerMessage(i18n.getMessage("success.logoutSuccess"), HttpStatus.OK);
    }
}