package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        String tokenName,
        String refreshTokenName,
        String currentUserKey,
        long accessTokenTtlMinutes,
        long refreshTokenTtlDays
) {

}
