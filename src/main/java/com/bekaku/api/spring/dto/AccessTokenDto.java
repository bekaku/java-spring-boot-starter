package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.LoginLogType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonRootName("apiClient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenDto {

    private Long id;
    private String ipAddredd;
    private String hostName;
    private String agent;
    private LoginLogType loginFrom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastestActive;
    boolean activeNow;

}
