package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("refreshToken")
public class RefreshTokenRequest {

    @NotBlank(message = "{error.validateRequire}")
    private String refreshToken;

    @NotBlank(message = "{error.validateRequire}")
    @Email(message = "{error.emailFormat}")
    private String email;
}
