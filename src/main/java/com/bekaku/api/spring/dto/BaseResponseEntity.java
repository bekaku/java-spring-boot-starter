package com.bekaku.api.spring.dto;

import org.springframework.http.HttpStatus;

public record BaseResponseEntity<T>
        (HttpStatus status,
         T data,
         String message) {


    public BaseResponseEntity(HttpStatus status) {
        this(status, null, null);
    }

    public BaseResponseEntity(HttpStatus status, T data) {
        this(status, data, null);
    }
    public BaseResponseEntity(HttpStatus status, String message) {
        this(status, null, message);
    }


}




