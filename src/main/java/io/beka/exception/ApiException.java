package io.beka.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final ApiError apiError;
    public ApiException(ApiError apiError) {
        super("");
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }
}