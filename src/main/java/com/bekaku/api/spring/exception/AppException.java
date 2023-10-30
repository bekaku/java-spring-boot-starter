package com.bekaku.api.spring.exception;

public class AppException extends RuntimeException {
    public AppException(String exMessage) {
        super(exMessage);
    }
}