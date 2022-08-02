package io.beka.controller.api;

import io.beka.exception.BaseResponseException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;


public class BaseApiController extends BaseResponseException {

    Logger logger = LoggerFactory.getLogger(BaseApiController.class);
    @Autowired
    private HttpServletRequest request;

    public ResponseEntity<Object> responseEntity(@Nullable Object o, HttpStatus status) {
        return new ResponseEntity<>(o, status);
    }

    public ResponseEntity<Object> responseEntity(HttpStatus status) {
        return new ResponseEntity<>(status);
    }

    public void initSearchParam() {
        logger.info("Params {}", request.getParameter("search"));
    }
}
