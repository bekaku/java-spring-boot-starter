package com.grandats.api.givedeefive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UserRequestDto {

    @NotEmpty(message = "{error.validateRequire}")
    @Email(message = "{error.emailFormat}")
    @Size(max = 100, message = "{error.SizeLimit100}")
    @JsonProperty("email")
    private String email;

    private String username;

    private boolean active = true;

    private Long[] selectedRoles = new Long[]{};

}
