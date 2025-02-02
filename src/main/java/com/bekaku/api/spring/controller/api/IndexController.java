package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.ConstantData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class IndexController extends BaseApiController {

    @GetMapping("/")
    public ResponseEntity<Object> index() {
        return responseEntity(new HashMap<String, Object>() {{
            put(ConstantData.SERVER_STATUS, true);
            put(ConstantData.SERVER_TIMESTAMP, DateUtil.getLocalDateTimeNow());
        }}, HttpStatus.OK);
    }

    @GetMapping(value = "/robots.txt", produces = "text/plain")
    public String getRobotsTxt() {
        return """
            User-agent: *
            Disallow: /
            """;
    }
}
