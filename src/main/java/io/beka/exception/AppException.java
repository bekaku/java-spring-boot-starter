package io.beka.exception;

public class AppException extends RuntimeException {
    public AppException(String exMessage) {
        super(exMessage);
    }
}