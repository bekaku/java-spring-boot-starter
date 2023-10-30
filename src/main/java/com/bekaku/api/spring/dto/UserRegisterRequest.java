package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonRootName("user")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterRequest extends UserRequestDto {

    @NotEmpty(message = "{error.validateRequire}")
    private String password;

    private boolean checkValidate = true;

}
