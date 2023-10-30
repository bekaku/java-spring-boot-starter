package com.grandats.api.givedeefive.controller.api;

import com.grandats.api.givedeefive.util.DateUtil;
import com.grandats.api.givedeefive.util.ConstantData;
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
}
