package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.LoginLogType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
