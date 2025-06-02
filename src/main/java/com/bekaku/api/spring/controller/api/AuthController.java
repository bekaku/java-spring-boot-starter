package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.*;
import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.Role;
import com.bekaku.api.spring.model.User;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j
@RequestMapping(path = "/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController extends BaseApiController {

    private final UserService userService;
    private final AuthService authService;
    private final AccessTokenService accessTokenService;
    private final EncryptService encryptService;
    private final RoleService roleService;
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


    @Value("${app.jwt.refresh-token-name}")
    String cookieJwtRefreshTokenName;

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@Valid @RequestBody UserRegisterRequest registerDto) {
        validateUserRegister(registerDto);

        //user can have many role
        Set<Role> roles = new HashSet<>();
        if (registerDto.getSelectedRoles().length > 0) {
            Optional<Role> role;
            for (long roleId : registerDto.getSelectedRoles()) {
                role = roleService.findById(roleId);
                role.ifPresent(roles::add);
            }
        }
//        else {
        //save defult role for new user
//            Optional<Role> role = roleService.findById(defaultRole);
//            role.ifPresent(roles::add);
//        }
        User user = new User();
        user.addNew(
                registerDto.getUsername(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                registerDto.isActive()
        );
        user.setRoles(roles);
        //encrypt pwd
        user.setPassword(encryptService.encrypt(user.getPassword(), user.getSalt()));
        userService.save(user);
        return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK, i18n.getMessage("success.logoutSuccess")), HttpStatus.OK);
    }

    private void validateUserRegister(@RequestBody UserRegisterRequest registerParam) {

        List<String> errors = new ArrayList<>();
        if (userService.findByUsername(registerParam.getUsername()).isPresent()) {
            errors.add(i18n.getMessage("error.validateDuplicateUsername", registerParam.getUsername()));
        }
        if (userService.findByEmail(registerParam.getEmail()).isPresent()) {
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
        Optional<User> user = userService.findByEmail(loginRequest.getEmailOrUsername());
        if (user.isEmpty()) {
            user = userService.findByUsername(loginRequest.getEmailOrUsername());
        }

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
        return authService.login(user.get(), loginRequest, apiClient.get(), userAgent, AppUtil.getIpaddress(request));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, RefreshTokenResponse tokenResponse) {
        log.info("setRefreshTokenCookie {}", tokenResponse.getRefreshToken());
        Cookie cookie = new Cookie(cookieJwtRefreshTokenName, tokenResponse.getRefreshToken());
        cookie.setMaxAge(AppUtil.getCookieMaxAgeDays(90));//cookie expired in 3 months
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // global cookie accessible every where
        cookie.setAttribute("SameSite", "None");
//        cookie.setAttribute("SameSite", "Lax");
        if (isProduction) {
            cookie.setDomain(appDomain);
        }
//        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
//        if (isProduction) {
//            response.setHeader("Set-Cookie", ConstantData.COOKIE_JWT_REFRESH_TOKEN + "=" + tokenResponse.getRefreshToken() + "; Max-Age=" + maxAge + ";SameSite=None; Path=/; Secure; HttpOnly");
//        } else {
//            response.setHeader("Set-Cookie", ConstantData.COOKIE_JWT_REFRESH_TOKEN + "=" + tokenResponse.getRefreshToken() + "; Max-Age=" + maxAge + "; SameSite=None; Path=/; HttpOnly");
//        }

    }

    private void deleteCookie(HttpServletResponse response) {
        // create a cookie
        Cookie cookie = new Cookie(cookieJwtRefreshTokenName, null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        if (isProduction) {
            cookie.setDomain(appDomain);
        }
        response.addCookie(cookie);
//        if (isProduction) {
//            response.setHeader("Set-Cookie",
//                    cookieName + "=; Max-Age=" + 0 + ";SameSite=None; Path=/; Secure; HttpOnly");
//        } else {
//            response.setHeader("Set-Cookie",
//                    cookieName + "=; Max-Age=" + 0 + "; SameSite=None; Path=/; HttpOnly");
//        }
    }

    @PostMapping("/refreshToken")
    public RefreshTokenResponse refreshToken(@Valid @RequestBody RefreshTokenRequest dto,
                                             @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                             @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {

        //TODO implement with cookie
//        String refreshTokenCookieManulRead = AppUtil.getCookieByName(request.getCookies(), ConstantData.COOKIE_JWT_REFRESH_TOKEN);

        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
//        Optional<String> jwtKey = jwtService.getAuthorizatoinTokenString(authorization);
        if (apiClient.isEmpty()) {
            log.error("refreshToken > apiClient.isEmpty()");
//            deleteCookie(response);
            throwUnauthorizes();
        }
        if (AppUtil.isEmpty(dto.getRefreshToken())) {
            log.error("refreshToken > AppUtil.isEmpty(dto.getRefreshToken())");
//            deleteCookie(response);
            throwUnauthorizes();
        }
        log.info("dto.getRefreshToken() :{}", dto.getRefreshToken());
        /*
        if (AppUtil.isEmpty(dto.getRefreshToken()) || jwtKey.isEmpty()) {
            log.error("refreshToken > AppUtil.isEmpty(dto.getRefreshToken()) || jwtKey.isEmpty()");
            deleteCookie(response);
             throwUnauthorizes();
        }

        Optional<String> subFromJwt = jwtService.getExpiredSubFromToken(jwtKey.get(), apiClient.get());
        log.info("subFromJwt :{}, dto.getRefreshToken :{} ",subFromJwt.orElse(null), dto.getRefreshToken());

        if (subFromJwt.isEmpty() || !subFromJwt.get().equals(dto.getRefreshToken())) {
            log.error("refreshToken > subFromJwt.isEmpty() || !subFromJwt.get().equals(dto.getRefreshToken())");
            deleteCookie(response);
             throwUnauthorizes();
        }
         */
        Optional<String> tokenKey = jwtService.getSubFromToken(dto.getRefreshToken(), apiClient.get());
        log.info("subFromJwt :{} ", tokenKey.orElse("sub null"));
        if (tokenKey.isEmpty()) {
            log.error("refreshToken > subFromJwt.isEmpty()");
//            deleteCookie(response);
            throwUnauthorizes();
        }
        Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(tokenKey.get(), false);
        if (accessToken.isEmpty()) {
            log.error("refreshToken > accessToken.isEmpty()");
//            deleteCookie(response);
            throwUnauthorizes();
        }
        log.info("refreshToken ,userId:{},  oldRefreshToken :{}", accessToken.get().getUser().getId(), tokenKey.get());

        //validate expred token
        boolean isExpired = accessTokenService.isTokenExpired(accessToken.get());
        if (isExpired) {
            log.error("refreshToken > accessToken isExpired");
            throwUnauthorizes();
        }

//        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
//        refreshTokenRequest.setRefreshToken(dto.getRefreshToken());
        //        RefreshTokenResponse tokenResponse = authService.refreshToken(accessToken.get(), apiClient.get(), AppUtil.getUserAgent(userAgent));
//        setRefreshTokenCookie(response, tokenResponse);
        return authService.refreshToken(accessToken.get(), apiClient.get(), AppUtil.getUserAgent(userAgent));
    }

    private void throwUnauthorizes() {
        throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"), "Session Expired"));
    }

    @PostMapping("/requestVerifyCodeToResetPwd")
    public ResponseEntity<Object> requestVerifyCodeToResetPwd(@Valid @RequestBody ForgotPasswordRequest reqBody,
                                                              @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                              @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) throws MessagingException {

        Optional<User> user = userService.findByEmail(reqBody.getEmail());
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
        Optional<User> user = userService.findByEmail(reqBody.getEmail());
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
        String newPassword = encryptService.encrypt(reqBody.getNewPassword(), accessToken.getUser().getSalt());
        userService.updatePasswordBy(accessToken.getUser(), newPassword);
        accessTokenService.delete(accessToken);
        return this.responseServerMessage(i18n.getMessage("helper.reset_pwd_ok", reqBody.getEmail()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(HttpServletResponse response,
                                                  @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                                  @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isPresent() && !ObjectUtils.isEmpty(refreshTokenRequest.getRefreshToken())) {
            Optional<AccessToken> accessToken = accessTokenService.findByToken(refreshTokenRequest.getRefreshToken().trim());
            accessToken.ifPresent(this::logoutProcess);
        }
//        deleteCookie(response);
        return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK, i18n.getMessage("success.logoutSuccess")), HttpStatus.OK);
    }

    private void logoutProcess(AccessToken token) {
        if (token.getFcmToken() != null) {
            List<AccessToken> allTokenByDevice = accessTokenService.findAllByFcmToken(token.getFcmToken());
            for (AccessToken t : allTokenByDevice) {
                accessTokenService.delete(t);
            }
        } else {
            accessTokenService.delete(token);
        }
    }

    @DeleteMapping("/removeAccessTokenSession")
    public ResponseEntity<Object> removeAccessTokenSession(@AuthenticationPrincipal UserDto userAuthen, @RequestParam(value = "id") Long id
    ) {
        Optional<AccessToken> accessToken = accessTokenService.findById(id);
        if (accessToken.isPresent() && Objects.equals(accessToken.get().getUser().getId(), userAuthen.getId())) {
            logoutProcess(accessToken.get());
        }
        return this.responseServerMessage(i18n.getMessage("success.logoutSuccess"), HttpStatus.OK);
    }
}