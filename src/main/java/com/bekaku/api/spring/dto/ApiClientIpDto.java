package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonRootName("data")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class ApiClientIpDto extends DtoId {
    private String ipAddress;
    private Boolean status;
}
