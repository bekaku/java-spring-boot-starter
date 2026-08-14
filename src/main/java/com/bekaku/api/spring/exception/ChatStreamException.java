package com.bekaku.api.spring.exception;

public class ChatStreamException extends RuntimeException {
    public ChatStreamException(String message, Throwable cause) {
        super(message, cause);
    }
}
