package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
