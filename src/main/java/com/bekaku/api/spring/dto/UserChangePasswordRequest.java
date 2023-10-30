package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonRootName("userChangePasswordRequest")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserChangePasswordRequest {

    @NotEmpty(message = "{error.validateRequire}")
    private String password;

    private String newPassword;

    private boolean logoutAllDevice;

}
