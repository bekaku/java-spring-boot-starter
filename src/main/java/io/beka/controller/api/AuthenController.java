package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.model.data.UserData;
import io.beka.model.data.UserWithToken;
import io.beka.model.dto.AuthenticationResponse;
import io.beka.model.dto.LoginDto;
import io.beka.model.dto.RefreshTokenRequest;
import io.beka.model.dto.UserRegisterDto;
import io.beka.model.entity.Role;
import io.beka.model.entity.User;
import io.beka.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final JwtService jwtService;

    @Value("${image.default}")
    String defaultImage;

    @Value("${default.user.role}")
    Long defaultRole;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody UserRegisterDto registerDto, BindingResult bindingResult) {
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
                encryptService.encrypt(registerDto.getPassword()),
                registerDto.getEmail(),
                true,
                defaultImage,
                roles
        );

        userService.save(user);
        Optional<UserData> userData = userService.findUserDataById(user.getId());
        if (userData.isPresent()) {
            UserWithToken userWithToken = new UserWithToken(userData.get(), jwtService.toToken("token"));
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("user", userWithToken);
            }});

        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("Error Message");
        }
    }

    private void checkInput(@Valid @RequestBody UserRegisterDto registerParam, BindingResult bindingResult) {
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
    public AuthenticationResponse login(@RequestBody LoginDto loginRequest, BindingResult bindingResult) {
        Optional<User> user = userService.findByEmail(loginRequest.getEmail());
        if (user.isEmpty()) {
            bindingResult.rejectValue("email", "INVALID", "invalid email or password");
            throw new InvalidRequestException(bindingResult);
        }

        if (!encryptService.check(loginRequest.getPassword(), user.get().getPassword()) && user.get().getStatus()) {
            bindingResult.rejectValue("password", "INVALID", "invalid email or password");
            throw new InvalidRequestException(bindingResult);
        }

        return authService.login(user.get(), loginRequest);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        accessTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Refresh Token Deleted Successfully!!");
    }
//    @PostMapping
//    public ResponseEntity userLogin(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {
//        Optional<User> optional = userService.findByEmail(loginDto.getEmail());
//        if (optional.isPresent() && encryptService.check(encryptService.encrypt(loginParam.getPassword()), optional.get().getPassword())) {
//            UserData userData = userService.findUserDataById(optional.get().getId()).get();
//            return ResponseEntity.ok(userResponse(new UserWithToken(userData, jwtService.toToken(optional.get()))));
//        } else {
//            bindingResult.rejectValue("password", "INVALID", "invalid email or password");
//            throw new InvalidRequestException(bindingResult);
//        }
//    }

//    private Map<String, Object> userResponse(UserWithToken userWithToken) {
//        return new HashMap<String, Object>() {{
//            put("user", userWithToken);
//        }};
//    }
}