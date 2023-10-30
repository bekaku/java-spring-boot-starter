package com.grandats.api.givedeefive.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import jakarta.validation.constraints.NotBlank;

import com.grandats.api.givedeefive.enumtype.LoginLogType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Getter
@JsonRootName("user")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class LoginRequest {

    private String emailOrUsername;
    @NotBlank(message = "{error.validateRequire}")
    private String password;
    private String fcmToken;
    private LoginLogType loginFrom;
}
