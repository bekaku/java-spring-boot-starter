package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Setter
@Getter
@JsonRootName("userRegister")
@NoArgsConstructor
public class UserRegisterRequest {

    @NotEmpty(message = "{error.validateRequire}")
    @Size(max = 100, message = "{error.SizeLimit100}")
    private String username;

    @NotEmpty(message = "{error.validateRequire}")
    private String password;

    @NotEmpty(message = "{error.validateRequire}")
    @Email(message = "{error.emailFormat}")
    @Size(max = 100, message = "{error.SizeLimit100}")
    private String email;

    private long[] selectedRoles;
}
