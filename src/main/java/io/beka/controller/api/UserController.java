package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.model.Page;
import io.beka.model.data.UserData;
import io.beka.model.data.UserWithToken;
import io.beka.model.dto.AuthenticationResponse;
import io.beka.model.entity.Role;
import io.beka.model.entity.User;
import io.beka.model.dto.UserRegisterDto;
import io.beka.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
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

    @Value("${image.default}")
    String defaultImage;

    @Value("${default.user.role}")
    Long defaultRole;

    @GetMapping("/current-user")
    public ResponseEntity<UserData> currentUser(@AuthenticationPrincipal UserData user) {
//        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("user", user);
//        }});
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserData>> getAllUser(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                     @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return new ResponseEntity<>(userService.findAllUserData(new Page(offset, limit)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody UserRegisterDto registerDto, BindingResult bindingResult) {
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
//                passwordEncoder.encode(registerDto.getPassword()),
                registerDto.getPassword(),
                registerDto.getEmail(),
                false,
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

    @GetMapping("/{id}")
    public ResponseEntity<UserData> getUserById(@PathVariable("id") long id) {
        Optional<UserData> userData = userService.findUserDataById(id);
        return userData.map(data -> new ResponseEntity<>(data, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserWithToken> updateUser(@PathVariable("id") long id, @RequestBody UserRegisterDto param) {
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
                UserWithToken userWithToken = new UserWithToken(userData.get(), jwtService.toToken("token"));
                return new ResponseEntity<>(userWithToken, HttpStatus.OK);
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
