package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "logging")
public record LoggingProperties(
        Map<String, String> file
) {
}
