package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@JsonRootName("user")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class LoginRequest {

        @NotBlank(message = "{error.validateRequire}")
        @Email(message = "{error.emailFormat}")
        private String email;

        @NotBlank(message = "{error.validateRequire}")
        private String password;
}
