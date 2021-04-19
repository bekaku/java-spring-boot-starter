package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;


@Setter
@Getter
@JsonRootName("userRegister")
@NoArgsConstructor
public class UserRegisterRequest {

    @NotEmpty(message = "{error.validateRequire}")
    private String username;

    @NotEmpty(message = "{error.validateRequire}")
    private String password;

    @NotEmpty(message = "{error.validateRequire}")
    @Email(message = "{error.emailFormat}")
    private String email;

    private String[] userRoles;
}
