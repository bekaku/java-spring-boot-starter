package io.beka.model.json.param;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonRootName("userRegister")
@NoArgsConstructor
public class UserRegisterParam {

    private String username;

    private String password;

    private String email;
}
