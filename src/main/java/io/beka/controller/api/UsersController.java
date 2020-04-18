package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.model.data.UserData;
import io.beka.model.data.UsersWithToken;
import io.beka.model.entity.Permissions;
import io.beka.model.entity.Users;
import io.beka.service.EncryptService;
import io.beka.service.JwtService;
import io.beka.service.UsersService;
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
@RequestMapping(path = "/api/users")
@RestController
public class UsersController {

    private final UsersService usersService;
    private final EncryptService encryptService;
    private final JwtService jwtService;

    @Value("${image.default}")
    String defaultImage;

    @PostMapping
    public ResponseEntity register(@Valid @RequestBody Users users, BindingResult bindingResult) {
        checkInput(users, bindingResult);

        users.setImage(defaultImage);
        users.setPassword(encryptService.encrypt(users.getPassword()));

        usersService.save(users);

        UserData userData = usersService.findUserDataById(users.getId()).get();

        UsersWithToken usersWithToken =new UsersWithToken(userData, jwtService.toToken(users));
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("users", usersWithToken);
        }});

    }
    private void checkInput(@Valid @RequestBody Users users, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (usersService.findUserDataByUsername(users.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "DUPLICATED", "duplicated username");
        }

        if (usersService.findUserDataByEmail(users.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "DUPLICATED", "duplicated email");
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }
}
