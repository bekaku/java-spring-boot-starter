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
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import jakarta.mail.MessagingException;
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
    private final EmailService emailService;
    private final I18n i18n;

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
//        RefreshTokenResponse tokenResponse = authService.login(user.get(), loginRequest, apiClient.get(), userAgent, AppUtil.getIpaddress(request));
//        setRefreshTokenCookie(response, tokenResponse);
//        return tokenResponse;
        return authService.login(user.get(), loginRequest, apiClient.get(), userAgent, AppUtil.getIpaddress(request));
    }

    private String getRefreshKeyBy(Long currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        return jwtProperties.getRefreshTokenName() + UNDER_SCORE + currentUserId;
    }

    private String getReJwtKeyBy(Long currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        return jwtProperties.getTokenName() + UNDER_SCORE + currentUserId;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, RefreshTokenResponse tokenResponse) {
        ResponseCookie jwtCookie = ResponseCookie.from(getReJwtKeyBy(tokenResponse.getUserId()), tokenResponse.getAuthenticationToken())
                .httpOnly(true)
                .secure(true) // true in prod (HTTPS), false in dev
                .path("/")
                .sameSite("None") // "Lax" for dev; "None" + secure for prod
                .maxAge(jwtService.expireRefreshSecond())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        ResponseCookie refreshCookie = ResponseCookie.from(getRefreshKeyBy(tokenResponse.getUserId()), tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true) // true in prod (HTTPS), false in dev
                .path("/")
                .sameSite("None") // "Lax" for dev; "None" + secure for prod
                .maxAge(jwtService.expireRefreshSecond())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

    }

    private void deleteCookie(HttpServletResponse response, Long currentUserId) {
        if (currentUserId != null) {
            ResponseCookie jwtCookie = ResponseCookie.from(getReJwtKeyBy(currentUserId), "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0) // Deletes cookie
                    .sameSite("None") // Must match how it was originally set
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            ResponseCookie refreshCookie = ResponseCookie.from(getRefreshKeyBy(currentUserId), "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0) // Deletes cookie
                    .sameSite("None") // Must match how it was originally set
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }
    }

    @PostMapping("/refreshToken")
    public RefreshTokenResponse refreshToken(@Valid @RequestBody RefreshTokenRequest dto,
                                             HttpServletRequest request,
                                             HttpServletResponse response,
                                             @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                             @RequestHeader(value = ConstantData.USER_AGENT) String userAgent,
                                             @RequestHeader(value = ConstantData.X_USER_ID, required = false, defaultValue = "0") Long currentUserId) {

        //TODO implement with cookie
//        String refreshTokenCookieKey = AppUtil.getCookieByName(request.getCookies(), getRefreshKeyBy(currentUserId));
//        String refreshTokenKey = refreshTokenCookieKey != null ? refreshTokenCookieKey : dto.getRefreshToken();
//        log.info("currentUserId: {}, refreshTokenCookie: {}, dto.getRefreshToken(): {}", currentUserId, refreshTokenCookieKey, dto.getRefreshToken());
        String refreshTokenKey = dto.getRefreshToken();
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            log.error("refreshToken > apiClient.isEmpty()");
//            deleteCookie(response);
            throwUnauthorizes();
        }
        if (AppUtil.isEmpty(refreshTokenKey)) {
            log.error("refreshToken > AppUtil.isEmpty(refreshTokenKey)");
//            deleteCookie(response);
            throwUnauthorizes();
        }
        log.info("dto.getRefreshToken() :{}", refreshTokenKey);
        Optional<String> tokenKey = jwtService.getSubFromToken(refreshTokenKey, apiClient.get());
//        log.info("subFromJwt :{} ", tokenKey.orElse("sub null"));
        if (tokenKey.isEmpty()) {
//            log.error("refreshToken > subFromJwt.isEmpty()");
//            deleteCookie(response);
            throwUnauthorizes();
        }
        Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(tokenKey.get(), false);
        if (accessToken.isEmpty()) {
//            log.error("refreshToken > accessToken.isEmpty()");
//            deleteCookie(response);
            throwUnauthorizes();
        }
//        log.info("refreshToken ,userId:{},  oldRefreshToken :{}", accessToken.get().getUser().getId(), tokenKey.get());

        //validate expred token
        boolean isExpired = accessTokenService.isTokenExpired(accessToken.get());
        if (isExpired) {
            throwUnauthorizes();
        }
//        RefreshTokenResponse tokenResponse = authService.refreshToken(accessToken.get(), apiClient.get(), AppUtil.getUserAgent(userAgent));
//        setRefreshTokenCookie(response, tokenResponse);
//        return tokenResponse;
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
                                                  @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                                  @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                  @RequestHeader(value = ConstantData.X_USER_ID, required = false, defaultValue = "0") Long currentUserId) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        String refreshTokenKey = AppUtil.getCookieByName(request.getCookies(), getRefreshKeyBy(currentUserId));
//        String refreshTokenKey = refreshTokenRequest.getRefreshToken();
        if (apiClient.isPresent() && !AppUtil.isEmpty(refreshTokenKey)) {
            Optional<String> tokenKey = jwtService.getSubFromToken(refreshTokenKey, apiClient.get());
            if (tokenKey.isPresent()) {
                Optional<AccessToken> accessToken = accessTokenService.findByToken(tokenKey.get());
                accessToken.ifPresent(this::logoutProcess);
            }
        }
        deleteCookie(response, currentUserId);
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