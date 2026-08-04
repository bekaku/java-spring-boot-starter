package com.bekaku.api.spring.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(value = { "hasError" })
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp= LocalDateTime.now();

    public ApiError(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
    public ApiError(HttpStatus status, String message, String ...errStrings) {
        super();
        this.status = status;
        this.message = message;
        this.errors = Arrays.asList(errStrings);
    }

    public ApiError(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Collections.singletonList(error);
    }
    public boolean isHasError(){
        return !errors.isEmpty();
    }

    public void setError(final String error) {
        errors = Collections.singletonList(error);
    }

}
