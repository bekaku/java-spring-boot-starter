package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.*;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.Role;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;


@RequestMapping(path = "/api/auth")
@RestController
public class AuthController extends BaseApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private EncryptService encryptService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ApiClientService apiClientService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private I18n i18n;

    @Value("${app.defaults.image}")
    String defaultImage;

    @Value("${app.defaults.role}")
    Long defaultRole;
    @Value("${environments.production}")
    boolean isProduction;
    @Value("${app.domain}")
    String appDomain;

    Logger logger = LoggerFactory.getLogger(AuthController.class);

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
        logger.info("setRefreshTokenCookie {}", tokenResponse.getRefreshToken());
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
    public RefreshTokenResponse refreshToken(HttpServletResponse response,
                                             HttpServletRequest request,
                                             @Valid @RequestBody RefreshTokenRequest dto,
                                             @RequestHeader(value = ConstantData.AUTHORIZATION) String authorization,
                                             @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                             @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {
        String refreshTokenCookieManulRead = AppUtil.getCookieByName(request.getCookies(), ConstantData.COOKIE_JWT_REFRESH_TOKEN);
        logger.info("refreshToken  RefreshTokenRequest :{}, refreshTokenCookieManulRead :{}", dto.getRefreshToken(), refreshTokenCookieManulRead);

        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        Optional<String> jwtKey = jwtService.getAuthorizatoinTokenString(authorization);
        if (apiClient.isEmpty()) {
            deleteCookie(response);
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"), "Api Client Not found"));
        }
        if (AppUtil.isEmpty(dto.getRefreshToken()) || jwtKey.isEmpty()) {
            deleteCookie(response);
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"), "Session Expired"));
        }

        Optional<String> subFromJwt = jwtService.getExpiredSubFromToken(jwtKey.get(), apiClient.get());
        logger.info("refreshToken subFromJwt:{}", subFromJwt.orElse(null));
        if (subFromJwt.isEmpty() || !subFromJwt.get().equals(dto.getRefreshToken())) {
            deleteCookie(response);
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"), "Session Expired"));
        }

        Optional<AccessToken> accessToken = accessTokenService.findByToken(dto.getRefreshToken());
        if (accessToken.isEmpty()) {
            deleteCookie(response);
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"), "Session Expired"));
        }
        //validate expred token
        LocalDateTime now = DateUtil.getLocalDateTimeNow();
        LocalDateTime expireDatetime = DateUtil.convertDateToLacalDatetime(accessToken.get().getExpiresAt());
        boolean isBefore = DateUtil.isBefore(expireDatetime, now);
        logger.info("expireDatetime :{}, now :{}, isBefore :{}", expireDatetime, now, isBefore);
        if (isBefore) {
            deleteCookie(response);
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"), "Session Expired"));
        }


        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(dto.getRefreshToken());
        //        RefreshTokenResponse tokenResponse = authService.refreshToken(accessToken.get(), apiClient.get(), AppUtil.getUserAgent(userAgent));
//        setRefreshTokenCookie(response, tokenResponse);
        return authService.refreshToken(accessToken.get(), apiClient.get(), AppUtil.getUserAgent(userAgent));
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
        deleteCookie(response);
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