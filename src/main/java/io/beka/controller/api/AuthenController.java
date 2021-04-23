package io.beka.controller.api;

import io.beka.configuration.I18n;
import io.beka.dto.*;
import io.beka.exception.ApiError;
import io.beka.exception.ApiException;
import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import io.beka.model.Role;
import io.beka.model.User;
import io.beka.service.*;
import io.beka.util.AppUtil;
import io.beka.util.ConstantData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
@RestController
public class AuthenController extends BaseApiController {

    private final UserService userService;
    private final AuthService authService;
    private final AccessTokenService accessTokenService;
    private final EncryptService encryptService;
    private final RoleService roleService;
    private final ApiClientService apiClientService;

    private final I18n i18n;

    @Value("${image.default}")
    String defaultImage;

    @Value("${default.user.role}")
    Long defaultRole;

    Logger logger = LoggerFactory.getLogger(AuthenController.class);

    @GetMapping("/test/{id}")
    public ResponseEntity<UserData> getUserById(@PathVariable("id") long id, @RequestParam String test) {
        Optional<UserData> userData = userService.findUserDataById(id);
        return userData.map(data -> new ResponseEntity<>(data, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/test")
    public ResponseEntity<Object> test(@Valid @RequestBody UserRegisterRequest registerDto) {

        final List<String> errors = new ArrayList<String>();
        errors.add("Error : " + i18n.getMessage("error.loginWrong"));
        final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, i18n.getMessage("error.error"), errors);
        if (apiError.isHasError()) {
            throw new ApiException(apiError);
        }
        return new ResponseEntity<>(registerDto, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@Valid @RequestBody UserRegisterRequest registerDto) {
        validateUserRegister(registerDto);
        //user can have many role
        Set<Role> roles = new HashSet<>();
        if (registerDto.getUserRoles().length > 0) {
            Optional<Role> role;
            for (String roleId : registerDto.getUserRoles()) {
                role = roleService.findById(Long.valueOf(roleId));
                role.ifPresent(roles::add);
            }
        }
//        else {
        //save defult role for new user
//            Optional<Role> role = roleService.findById(defaultRole);
//            role.ifPresent(roles::add);
//        }
        User user = new User(
                registerDto.getUsername(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                true,
                defaultImage,
                roles
        );
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
        if (errors.size() > 0) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"), errors));
        }
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody LoginRequest loginRequest,
                                        @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                        @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {

        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);

        if (apiClient.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"),
                    "Api Client Not found"));
        }
        Optional<User> user = userService.findByEmail(loginRequest.getEmail());
        if (user.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, i18n.getMessage("error.error"),
                    i18n.getMessage("error.userNotFoundWithEmail", loginRequest.getEmail())));
        }

        if (!encryptService.check(loginRequest.getPassword(), user.get().getPassword()) || !user.get().getStatus()) {
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"),
                    i18n.getMessage("error.loginWrong")));
        }

        return authService.login(user.get(), loginRequest, apiClient.get(), userAgent);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                                @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName,
                                                @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"),
                    "Api Client Not found"));
        }
        return authService.refreshToken(refreshTokenRequest, apiClient.get(), AppUtil.getUserAgent(userAgent));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                                  @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"),
                    "Api Client Not found"));
        }
        Optional<AccessToken> accessToken = accessTokenService.findByToken(refreshTokenRequest.getRefreshToken());
        if (accessToken.isPresent()) {
            AccessToken token = accessToken.get();
            token.setRevoked(true);
            token.setToken(null);
            accessTokenService.update(token);
        }
//        accessToken.ifPresent(accessTokenService::delete);
        return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK, i18n.getMessage("success.logoutSuccess")), HttpStatus.OK);
    }
}