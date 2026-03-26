package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logging.file")
public record LoggingFileProperties(
        String path,
        int maxHistory,
        String totalSizeCap,
        Boolean cleanHistoryOnStart
        ) {

}
