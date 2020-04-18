package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.model.entity.AccessToken;
import io.beka.model.entity.Permissions;
import io.beka.model.entity.Users;
import io.beka.service.AccessTokenService;
import io.beka.service.UsersService;
import io.beka.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.DateUtils;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@RequestMapping(path = "/api/test")
@RequiredArgsConstructor
@RestController
public class TestController {
    private final AccessTokenService accessTokenService;
    private final UsersService usersService;
    @Value("${jwt.sessionTime}")
    int sessionTime;

    @Value("${jwt.secret}")
    String jwtSecret;

    @PostMapping
    public ResponseEntity create() {

        Users users = usersService.findById(Long.valueOf("9")).get();

//        Date expires = new Date(System.currentTimeMillis() + (sessionTime > 0 ? sessionTime : DateUtil.MILLS_IN_YEAR));
//        AccessToken accessToken = new AccessToken(
//                users, jwtSecret, expires, false
//        );
//        accessTokenService.save(accessToken);

        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("accessToken", users.getAccessTokens());
        }});
    }
}
