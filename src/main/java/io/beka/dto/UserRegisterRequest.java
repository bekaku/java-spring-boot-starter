package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
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
