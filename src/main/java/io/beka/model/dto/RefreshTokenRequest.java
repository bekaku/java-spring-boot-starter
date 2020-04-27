package io.beka.model.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("refreshToken")
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
    private String email;
}
