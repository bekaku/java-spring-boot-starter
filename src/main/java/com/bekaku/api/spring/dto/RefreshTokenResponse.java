package com.bekaku.api.spring.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponse {
    private Long userId;
    private String authenticationToken;
    private String refreshToken;
    private Date expiresAt;
}
