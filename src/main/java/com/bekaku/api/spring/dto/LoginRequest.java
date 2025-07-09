package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.LoginLogType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@JsonRootName("user")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class LoginRequest extends EmailOrUsernameRequest {

    @NotBlank(message = "{error.validateRequire}")
    private String password;
    private String fcmToken;
    private String deviceId;
    private LoginLogType loginFrom;
}
