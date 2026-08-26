package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.cron")
public record AppCronProperties(
        String testExpression,
        String cleanFileExpression,
        boolean cleanOldFile,
        boolean cleanOldTempChunks
) {
}
