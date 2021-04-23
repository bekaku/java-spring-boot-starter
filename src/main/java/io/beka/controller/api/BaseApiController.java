package io.beka.controller.api;

import io.beka.configuration.I18n;
import io.beka.exception.ApiError;
import io.beka.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.List;

public class BaseApiController {

    @Autowired
    private I18n i18n;

    public ApiException responseError(HttpStatus status, String message, String error) {
        return new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), error));
    }

    public ApiException responseError(HttpStatus status, String message, List<String> errors) {
        return new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), errors));
    }

    public ApiException responseError(HttpStatus status, String message, String... errors) {
        return new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), errors));
    }

    public ResponseEntity<Object> responseEntity(@Nullable Object o, HttpStatus status) {
        return new ResponseEntity<>(o, status);
    }
    public ResponseEntity<Object> responseEntity(HttpStatus status) {
        return new ResponseEntity<>(status);
    }
}
