package com.bekaku.api.spring.exception;

public class DocumentIngestionException extends RuntimeException{
    public DocumentIngestionException(String message) {
        super(message);
    }

    public DocumentIngestionException(String message, Throwable cause) {
        super(message, cause);
    }
}
