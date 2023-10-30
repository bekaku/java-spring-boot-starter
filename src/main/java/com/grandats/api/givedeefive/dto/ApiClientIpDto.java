package com.grandats.api.givedeefive.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonRootName("apiClientIp")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class ApiClientIpDto {
    private Long id;
    private String ipAddress;
    private Boolean status;
}
