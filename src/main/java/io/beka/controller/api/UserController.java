package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.model.entity.User;
import io.beka.model.json.param.UserRegisterParam;
import io.beka.service.EncryptService;
import io.beka.service.JwtService;
import io.beka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity register(@Valid @RequestBody UserRegisterParam registerParam, BindingResult bindingResult) {
        checkInput(registerParam, bindingResult);

        User user = new User();
        user.setImage(defaultImage);
        System.out.println("original PWD : "+registerParam.getPassword());
        user.setPassword(encryptService.encrypt(registerParam.getPassword()));

//        userService.save(user);
//
//        UserData userData = userService.findUserDataById(user.getId()).get();
//
//        UserWithToken userWithToken =new UserWithToken(userData, jwtService.toToken(user));
        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("user", userWithToken);
            put("userParam", registerParam);
        }});

    }
    private void checkInput(@Valid @RequestBody UserRegisterParam registerParam, BindingResult bindingResult) {
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

    //test
    @RequestMapping("/greeting")
    public @ResponseBody String greeting() {
        return userService.greet();
    }
}
