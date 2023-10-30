package com.bekaku.api.spring.exception;

import com.bekaku.api.spring.configuration.I18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

public class BaseResponseException {
    @Autowired
    private I18n i18n;

    public ApiException responseErrorNotfound() {
        return this.responseError(HttpStatus.NOT_FOUND, null, i18n.getMessage("error.dataNotfound"));
    }
    public ApiException responseErrorUnauthorized() {
        return this.responseError(HttpStatus.UNAUTHORIZED, null, i18n.getMessage("error.401"));
    }
    public ApiException responseErrorBadRequest() {
        return this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.error"));
    }

    public ApiException responseErrorNotfound(String data) {
        return this.responseError(HttpStatus.NOT_FOUND, null, i18n.getMessage("error.dataNotfound") + " " + data);
    }

    public ApiException responseErrorDuplicate(String data) {
        return this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.validateDuplicate", data));
    }
    public ApiException responseError(String error) {
        return this.responseError(HttpStatus.BAD_REQUEST, null, error);
    }

    public ApiException responseError(HttpStatus status, String message, String error) {
        return new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), error));
    }

    public void throwError(HttpStatus status, String message, String error) {
        throw new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), error));
    }

    public void throwError(HttpStatus status, String message, List<String> errors) {
        throw new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), errors));
    }

    public void throwError(HttpStatus status, String message, String... errors) {
        throw new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), errors));
    }

    public ApiException responseError(HttpStatus status, String message, List<String> errors) {
        return new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), errors));
    }

    public ApiException responseError(HttpStatus status, String message, String... errors) {
        return new ApiException(new ApiError(status, message != null ? message : i18n.getMessage("error.error"), errors));
    }
}
