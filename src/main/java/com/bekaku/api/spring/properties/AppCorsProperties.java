package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record AppCorsProperties(
        List<String> allowedOrigins //app.allowed-origins
) {
}
