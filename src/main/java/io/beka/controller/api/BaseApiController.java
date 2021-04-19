package io.beka.controller.api;

import io.beka.exception.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseApiController {
    public ResponseEntity<Object> responseError(ApiError apiError) {
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
