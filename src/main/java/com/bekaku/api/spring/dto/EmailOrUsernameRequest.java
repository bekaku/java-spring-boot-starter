package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@JsonRootName("emailOrUsername")
@Getter
@Setter
public class EmailOrUsernameRequest {
    @NotBlank(message = "{error.validateRequire}")
    private String emailOrUsername;
}
