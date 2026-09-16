package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiClientDto extends DtoId {
    @NotEmpty(message = "{error.NotEmpty}")
    @Size(max = 100, message = "{error.SizeLimit100}")
    private String apiName;
    private String apiTokenMask;
    private String key;
    private Boolean byPass;
    private Boolean status;

    /** Optional owner. Required only to authenticate this client with X-API-KEY. */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appUserId;

    /** Optional X-API-KEY expiry. Null never expires. */
    private Instant expiresAt;

    private List<ApiClientIpDto> apiClientDtoList;
}
