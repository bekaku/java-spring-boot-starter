package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.model.data.UserData;
import io.beka.model.data.UserWithToken;
import io.beka.model.entity.User;
import io.beka.service.EncryptService;
import io.beka.service.JwtService;
import io.beka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@RequiredArgsConstructor
@RequestMapping(path = "/api/user")
@RestController
public class UserController {

    private final UserService userService;
    private final EncryptService encryptService;
    private final JwtService jwtService;

    @Value("${image.default}")
    String defaultImage;

    @PostMapping
    public ResponseEntity register(@Valid @RequestBody User user, BindingResult bindingResult) {
        checkInput(user, bindingResult);

        user.setImage(defaultImage);
        System.out.println("original PWD : "+user.getPassword());
        user.setPassword(encryptService.encrypt(user.getPassword()));

//        userService.save(user);
//
//        UserData userData = userService.findUserDataById(user.getId()).get();
//
//        UserWithToken userWithToken =new UserWithToken(userData, jwtService.toToken(user));
        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("user", userWithToken);
            put("userParam", user);
        }});

    }
    private void checkInput(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (userService.findUserDataByUsername(user.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "DUPLICATED", "duplicated username");
        }

        if (userService.findUserDataByEmail(user.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "DUPLICATED", "duplicated email");
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }
}
