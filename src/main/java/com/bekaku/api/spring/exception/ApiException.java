package com.bekaku.api.spring.exception;

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