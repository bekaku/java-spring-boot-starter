package io.beka.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Data
public class ResponseMessage {

    public ResponseMessage(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    private HttpStatus status;
    private String message;
}
