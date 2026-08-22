package com.bekaku.api.spring.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiClientDto extends DtoId {

    private Long id;

    @NotEmpty(message = "{error.NotEmpty}")
    @Size(max = 100, message = "{error.SizeLimit100}")
    private String apiName;
    private Boolean byPass;
    private Boolean status;

    private List<ApiClientIpDto> apiClientDtoList;
}
