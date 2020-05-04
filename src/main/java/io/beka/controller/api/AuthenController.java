package io.beka.controller.api;

import io.beka.exception.AppException;
import io.beka.exception.InvalidRequestException;
import io.beka.model.dto.UserData;
import io.beka.model.dto.UserWithToken;
import io.beka.model.dto.AuthenticationResponse;
import io.beka.model.dto.LoginRequest;
import io.beka.model.dto.RefreshTokenRequest;
import io.beka.model.dto.UserRegisterRequest;
import io.beka.model.entity.ApiClient;
import io.beka.model.entity.Role;
import io.beka.model.entity.User;
import io.beka.service.*;
import io.beka.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
@RestController
public class AuthenController {

    private final UserService userService;
    private final AuthService authService;
    private final AccessTokenService accessTokenService;
    private final EncryptService encryptService;
    private final RoleService roleService;
    private final ApiClientService apiClientService;


    @Value("${image.default}")
    String defaultImage;

    @Value("${default.user.role}")
    Long defaultRole;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody UserRegisterRequest registerDto, BindingResult bindingResult) {
        checkInput(registerDto, bindingResult);

        //user can have manu role
        Set<Role> roles = new HashSet<>();
        if (registerDto.getUserRoles().length > 0) {
            Optional<Role> role = Optional.empty();
            for (String roleId : registerDto.getUserRoles()) {
                role = roleService.findById(Long.valueOf(roleId));
                role.ifPresent(roles::add);
            }
        } else {
            //save defult role for new user
            Optional<Role> role = roleService.findById(defaultRole);
            role.ifPresent(roles::add);
        }

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

        Optional<UserData> userData = userService.findUserDataById(user.getId());
        if (userData.isPresent()) {
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("user", userData);
            }});
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("Error Message");
        }
    }

    private void checkInput(@Valid @RequestBody UserRegisterRequest registerParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }

        if (StringUtils.isEmpty(registerParam.getUsername())) {
            bindingResult.rejectValue("username", "REQUIRED", "can't be empty");
        }

        if (userService.findUserDataByUsername(registerParam.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "DUPLICATED", "duplicated username");
        }

        if (userService.findUserDataByEmail(registerParam.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "DUPLICATED", "duplicated email");
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest, BindingResult bindingResult,
                                        @RequestHeader(value = "Accept-ApiClient") String apiClientName,
                                        @RequestHeader(value = "User-Agent") String userAgent) {

        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);

        if (apiClient.isEmpty()) {
            throw new AppException("Api Client Not found");
        }
        Optional<User> user = userService.findByEmail(loginRequest.getEmail());
        if (user.isEmpty()) {
            bindingResult.rejectValue("email", "INVALID", "user not found with email " + loginRequest.getEmail());
            throw new InvalidRequestException(bindingResult);
        }

        if (!encryptService.check(
                encryptService.encrypt(loginRequest.getPassword(), user.get().getSalt()), user.get().getPassword())
                || !user.get().getStatus()) {
            throw new AppException("invalid email or password");
        }

        return authService.login(user.get(), loginRequest, apiClient.get(), userAgent);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                                @RequestHeader(value = "Accept-ApiClient") String apiClientName,
                                                @RequestHeader(value = "User-Agent") String userAgent) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            throw new AppException("Api Client Not found");
        }
        return authService.refreshToken(refreshTokenRequest, apiClient.get(), AppUtil.getUserAgent(userAgent));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        accessTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Refresh Token Deleted Successfully!!");
    }
}