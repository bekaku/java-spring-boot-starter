package io.beka.controller.api;

import io.beka.model.User;
import io.beka.service.AccessTokenService;
import io.beka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequestMapping(path = "/api/test")
@RequiredArgsConstructor
@RestController
public class TestController {
    private final AccessTokenService accessTokenService;
    private final UserService userService;

    @Value("${jwt.sessionTime}")
    int sessionTime;

    @Value("${jwt.secret}")
    String jwtSecret;

    @PostMapping
    public ResponseEntity create() {

        User user = userService.findById(Long.valueOf("9")).get();

//        Date expires = new Date(System.currentTimeMillis() + (sessionTime > 0 ? sessionTime : DateUtil.MILLS_IN_YEAR));
//        AccessToken accessToken = new AccessToken(
//                users, jwtSecret, expires, false
//        );
//        accessTokenService.save(accessToken);

        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("accessToken", user.getAccessTokens());
        }});
    }
}
