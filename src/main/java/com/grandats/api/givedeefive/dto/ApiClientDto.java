package com.grandats.api.givedeefive.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonRootName("apiClient")
@Data
@NoArgsConstructor
public class ApiClientDto {

    private Long id;

    @NotEmpty(message = "{error.NotEmpty}")
    @Size(max = 100, message = "{error.SizeLimit100}")
    private String apiName;
    private Boolean byPass;
    private Boolean status;

    private List<ApiClientIpDto> apiClientDtoList;
}
