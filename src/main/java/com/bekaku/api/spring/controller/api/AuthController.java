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
import com.bekaku.api.spring.util.CookieUtil;
import com.bekaku.api.spring.util.HashUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
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
    private final IdentityLinkService identityLinkService;
    private final CookieUtil cookieUtil;

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

        ApiClient apiClient = validateApiClient(apiClientName);
        AppUser user = validateLogin(loginRequest);
        RefreshTokenResponse tokenResponse = authService.login(user, loginRequest, apiClient, userAgent, AppUtil.getIpaddress(request));
        setAuthCookie(response, tokenResponse);
        return tokenResponse;
//        return authService.login(user.get(), loginRequest, apiClient.get(), userAgent, AppUtil.getIpaddress(request));
    }

    private ApiClient validateApiClient(String apiClientName) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);

        if (apiClient.isEmpty()) {
            throw this.responseError(HttpStatus.BAD_REQUEST,
                    i18n.getMessage("error.apiClientNotFound"));
        }

        return apiClient.get();
    }

    @PostMapping("/loginApi")
    public RefreshTokenResponse loginApi(@Valid @RequestBody LoginRequest loginRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                         @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {

        ApiClient apiClient = validateApiClient(apiClientName);

        AppUser user = validateLogin(loginRequest);
        return authService.login(user, loginRequest, apiClient, userAgent, AppUtil.getIpaddress(request));
    }

    @PostMapping("/linkedAccounts")
    public ResponseEntity<List<AppUserDto>> getLinkedAccounts(@AuthenticationPrincipal AppUserDto currentUser) {
        Optional<AppUser> current = appUserService.findById(currentUser.getId());
        if (current.isEmpty()) {
            throw this.responseErrorForbidden();
        }
        List<AppUserDto> accounts = identityLinkService.getLinkedAccounts(current.get());
        return this.responseEntity(accounts, HttpStatus.OK);
    }

    @PostMapping("/linkAccount")
    public ResponseEntity<RefreshTokenResponse> linkAccount(@AuthenticationPrincipal AppUserDto currentUser,
                                                            @RequestBody LoginRequest loginRequest,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response,
                                                            @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                            @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {

        Optional<AppUser> current = appUserService.findById(currentUser.getId());
        if (current.isEmpty()) {
            throw this.responseErrorForbidden();
        }
        ApiClient apiClient = validateApiClient(apiClientName);
        AppUser targetUser = validateLogin(loginRequest);
        identityLinkService.linkAccount(current.get(), targetUser);
        RefreshTokenResponse tokenResponse = authService.login(targetUser, apiClient, userAgent, AppUtil.getIpaddress(request));
        setAuthCookie(response, tokenResponse);
        return this.responseEntity(tokenResponse, HttpStatus.OK);
    }

    @PostMapping("/switchAccount/{targetUserId}")
    public ResponseEntity<?> switchAccount(@AuthenticationPrincipal AppUserDto currentUser,
                                           @PathVariable("targetUserId") Long targetUserId,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                           @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {
        String targetRefreshCookie = AppUtil.getCookieByName(request.getCookies(), appProperties.jwt().refreshTokenName() + targetUserId);
        if (!AppUtil.isEmpty(targetRefreshCookie)) {
            var accessTokenOpt = accessTokenService.findByToken(targetRefreshCookie);
            if (accessTokenOpt.isPresent()) {

                if (!accessTokenService.isTokenExpired(accessTokenOpt.get())) {

                    ResponseCookie currentUserCookie = cookieUtil.setCookie(appProperties.jwt().currentUserKey(), targetUserId.toString(),
                            Duration.ofDays(appProperties.jwt().refreshTokenTtlDays()), "/", true);
                    response.addHeader(HttpHeaders.SET_COOKIE, currentUserCookie.toString());
                    return this.responseEntity(HttpStatus.OK);
                } else {
                    accessTokenService.delete(accessTokenOpt.get());
                }
            }
        }

        identityLinkService.validateSwitchAccount(currentUser.getId(), targetUserId);
        AppUser targetUser = appUserService.findById(targetUserId).orElseThrow();
        ApiClient apiClient = validateApiClient(apiClientName);
        RefreshTokenResponse tokenResponse = authService.login(targetUser, apiClient, userAgent, AppUtil.getIpaddress(request));
        setAuthCookie(response, tokenResponse);

        return this.responseEntity(tokenResponse, HttpStatus.OK);
    }

    @PostMapping("/removeLinkAccount/{targetUserId}")
    public ResponseEntity<?> removeLinkAccount(@AuthenticationPrincipal AppUserDto currentUser,
                                               @PathVariable("targetUserId") Long targetUserId,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        identityLinkService.removeLinkAccount(currentUser.getId(), targetUserId);
        deleteCookie(request, response, targetUserId, currentUser.getId().equals(targetUserId));
        return this.responseEntity(HttpStatus.OK);
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
        return appProperties.jwt().refreshTokenName() + currentUserId;
    }

    private String getJwtKeyBy(Long currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        return appProperties.jwt().tokenName() + currentUserId;
    }

    private void setAuthCookie(HttpServletResponse response, RefreshTokenResponse tokenResponse) {

        ResponseCookie accessToken = cookieUtil.setCookie(getJwtKeyBy(tokenResponse.getUserId()), tokenResponse.getAuthenticationToken(),
                Duration.ofMinutes(appProperties.jwt().accessTokenTtlMinutes()), "/", true);
        ResponseCookie refreshToken = cookieUtil.setCookie(getRefreshKeyBy(tokenResponse.getUserId()), tokenResponse.getRefreshToken(),
                Duration.ofDays(appProperties.jwt().refreshTokenTtlDays()), "/", true);
        ResponseCookie currentUser = cookieUtil.setCookie(appProperties.jwt().currentUserKey(), tokenResponse.getUserId().toString(),
                Duration.ofDays(appProperties.jwt().refreshTokenTtlDays()), "/", true);

        response.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, currentUser.toString());
    }

    private void deleteCookie(HttpServletRequest request, HttpServletResponse response, Long currentUserId, boolean setCurrentToAnothor) {
        if (currentUserId != null) {
            deleteCookieByName(response, getJwtKeyBy(currentUserId), "/", true);
            deleteCookieByName(response, getRefreshKeyBy(currentUserId), "/", true);
            deleteCookieByName(response, appProperties.jwt().currentUserKey(), "/", false);
            if (setCurrentToAnothor) {
                setCurrentUserToAnother(request, response, currentUserId);
            }
        }
    }

    private void deleteCookieByName(HttpServletResponse response, String cookieName, String path, boolean httponly) {
        if (AppUtil.isEmpty(cookieName)) {
            return;
        }
        ResponseCookie cookie = cookieUtil.clearCookie(cookieName, path, httponly);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void setCurrentUserToAnother(HttpServletRequest request, HttpServletResponse response, Long loggedOutUserId) {
        if (request.getCookies() == null) return;

        String refreshCookiePrefix = appProperties.jwt().refreshTokenName();
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
                    deleteCookieByName(response, cookie.getName(), "/api/auth", true);
                    continue;
                }

                String refreshTokenValue = cookie.getValue();
                Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(refreshTokenValue, false);

                if (accessToken.isPresent() && !accessTokenService.isTokenExpired(accessToken.get())) {
                    if (!isCurrentUserSet) {

                        ResponseCookie currentUserCokie = cookieUtil.setCookie(appProperties.jwt().currentUserKey(), otherUserIdStr,
                                Duration.ofDays(appProperties.jwt().refreshTokenTtlDays()), "/", true);
                        response.addHeader(HttpHeaders.SET_COOKIE, currentUserCokie.toString());

                        isCurrentUserSet = true;
                    }
                } else {
                    deleteCookieByName(response, getJwtKeyBy(otherUserId), "/", true);
                    deleteCookieByName(response, getRefreshKeyBy(otherUserId), "/api/auth", true);
                    accessToken.ifPresent(accessTokenService::delete);
                }
            }
        }
    }

    @PostMapping("/refreshToken")
    public RefreshTokenResponse refreshToken(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                             @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {

        String ckUserId = AppUtil.getCookieByName(request.getCookies(), appProperties.jwt().currentUserKey());
        log.info("refreshToken: ckUserId:{}", ckUserId);
        if (AppUtil.isEmpty(ckUserId) || !NumberUtils.isParsable(ckUserId)) {
            throwUnauthorizes();
        }
        Long currentUserId = Long.valueOf(ckUserId);
        String refreshTokenKey = AppUtil.getCookieByName(request.getCookies(), getRefreshKeyBy(currentUserId));
        log.info("refreshToken: refreshTokenKey:{}", refreshTokenKey);
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            log.info("refreshToken: apiClient.isEmpty()");
            deleteCookie(request, response, currentUserId, true);
            throwUnauthorizes();
        }
        if (AppUtil.isEmpty(refreshTokenKey)) {
            log.info("refreshToken: refreshTokenKey.isEmpty()");
            deleteCookie(request, response, currentUserId, true);
            throwUnauthorizes();
        }
        Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(refreshTokenKey, false);
        if (accessToken.isEmpty()) {
            log.info("refreshToken: accessToken.isEmpty()");
            deleteCookie(request, response, currentUserId, true);
            throwUnauthorizes();
        }

        //validate expred token
        boolean isExpired = accessTokenService.isTokenExpired(accessToken.get());
        if (isExpired) {
            log.info("refreshToken: accessToken isExpired");
            deleteCookie(request, response, currentUserId, true);
            throwUnauthorizes();
        }
        RefreshTokenResponse tokenResponse = authService.refreshToken(accessToken.get(), apiClient.get(), AppUtil.getUserAgent(userAgent));
        setAuthCookie(response, tokenResponse);
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
//        Optional<String> tokenKey = jwtService.getSubFromToken(refreshTokenKey, apiClient.get());
//        if (tokenKey.isEmpty()) {
//            throwUnauthorizes();
//        }
        Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(refreshTokenKey, false);
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
                                                  @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {

        String ckUserId = AppUtil.getCookieByName(request.getCookies(), appProperties.jwt().currentUserKey());
        if (!AppUtil.isEmpty(ckUserId)) {
            Long currentUserId = Long.valueOf(ckUserId);
            Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
            String refreshTokenKey = AppUtil.getCookieByName(request.getCookies(), getRefreshKeyBy(currentUserId));
            if (apiClient.isPresent() && !AppUtil.isEmpty(refreshTokenKey)) {
                Optional<AccessToken> accessToken = accessTokenService.findByToken(refreshTokenKey);
                accessToken.ifPresent(this::logoutProcess);
            }
            deleteCookie(request, response, currentUserId, true);
        }
        return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK, i18n.getMessage("success.logoutSuccess")), HttpStatus.OK);
    }

    @PostMapping("/logoutApi")
    public ResponseEntity<ResponseMessage> logoutApi(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
            @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
            @RequestHeader(value = ConstantData.X_USER_ID, required = false, defaultValue = "0") Long currentUserId) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        String refreshTokenKey = refreshTokenRequest.getRefreshToken();
        if (apiClient.isPresent() && !AppUtil.isEmpty(refreshTokenKey)) {
            Optional<AccessToken> accessToken = accessTokenService.findByToken(refreshTokenKey);
            accessToken.ifPresent(this::logoutProcess);
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