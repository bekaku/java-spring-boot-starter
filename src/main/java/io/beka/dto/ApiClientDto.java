package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@JsonRootName("apiClient")
@Getter
@NoArgsConstructor
public class ApiClientDto {

    private Long id;
    @NotEmpty(message = "{title}")
    private String apiName;
    private Boolean byPass;
    private Boolean status;


}
