package io.beka.model.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonRootName("userRegister")
@NoArgsConstructor
public class UserRegisterRequest {

    private String username;

    private String password;

    private String email;

    private String[] userRoles;
}
