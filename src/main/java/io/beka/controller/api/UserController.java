package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.dto.Paging;
import io.beka.dto.UserData;
import io.beka.dto.UserRegisterRequest;
import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;
import io.beka.model.User;
import io.beka.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping(path = "/api/user")
@RestController
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final AccessTokenService accessTokenService;
    private final ApiClientIpService apiClientIpService;

    @Value("${image.default}")
    String defaultImage;

    @Value("${default.user.role}")
    Long defaultRole;

    @GetMapping("/current-user")
    public ResponseEntity<Object> currentUser(@AuthenticationPrincipal UserData user) {
//        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("user", user);
//        }});
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserData>> getAllUser(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                     @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return new ResponseEntity<>(userService.findAllUserData(new Paging(offset, limit)), HttpStatus.OK);
    }

    private void checkInput(@Valid @RequestBody UserRegisterRequest registerParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }

        if (ObjectUtils.isEmpty(registerParam.getUsername())) {
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

    @GetMapping("/{id}")
    public ResponseEntity<UserData> getUserById(@PathVariable("id") long id) {
        Optional<UserData> userData = userService.findUserDataById(id);
        return userData.map(data -> new ResponseEntity<>(data, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserData> updateUser(@PathVariable("id") long id, @RequestBody UserRegisterRequest param) {
        Optional<User> user = userService.findById(id);

        if (user.isPresent()) {
            User userUpdate = user.get();
            userUpdate.update(
                    param.getUsername(),
                    param.getPassword(),
                    param.getEmail(),
                    false,
                    defaultImage
            );

            Optional<UserData> userData = userService.findUserDataById(userUpdate.getId());
            if (userData.isPresent()) {
                return new ResponseEntity<>(userData.get(), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            try {
                userService.delete(user.get());
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
