package io.beka.controller.test;

import io.beka.controller.api.BaseApiController;
import io.beka.properties.AppProperties;
import io.beka.properties.MailProperties;
import io.beka.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequestMapping(path = "/test")
@RestController
@RequiredArgsConstructor
public class TestApiController extends BaseApiController {

    Logger logger = LoggerFactory.getLogger(TestApiController.class);

    //    private final MailProperties mailProperties;
    private final AppProperties appProperties;

    @GetMapping
    public ResponseEntity<Object> testGet() {
        logger.info("testGet");
        return this.responseEntity(new HashMap<String, Object>() {{
            put("camelToSnake", AppUtil.camelToSnake("ApiClient"));
        }}, HttpStatus.OK);
    }

    @GetMapping("/properties")
    public ResponseEntity<Object> testProperties() {
        return this.responseEntity(new HashMap<String, Object>() {{
            put("normal-properties", appProperties != null ? appProperties.getVersion() : null);
            put("object-properties", appProperties != null ? appProperties.getMailConfig() : null);
            put("list-properties", appProperties != null ? appProperties.getDefaultRecipients() : null);
            put("map-Properties", appProperties != null ? appProperties.getAdditionalHeaders() : null);
            put("object-list-properties", appProperties != null ? appProperties.getMenus() : null);
        }}, HttpStatus.OK);
    }
}
