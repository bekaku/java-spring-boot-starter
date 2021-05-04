package io.beka.controller.api;

import io.beka.exception.BaseResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

public class BaseApiController extends BaseResponseException {

    public ResponseEntity<Object> responseEntity(@Nullable Object o, HttpStatus status) {
        return new ResponseEntity<>(o, status);
    }

    public ResponseEntity<Object> responseEntity(HttpStatus status) {
        return new ResponseEntity<>(status);
    }
}
