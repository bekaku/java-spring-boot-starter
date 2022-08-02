package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
