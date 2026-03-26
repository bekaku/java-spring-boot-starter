package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        int sessionTime,
        int sessionRefreshTime,
        String tokenName,
        String refreshTokenName) {

}
