package com.bekaku.api.spring.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "logging.file")
public class LoggingFileProperties {
    private String path;
    private int maxHistory;
    private String totalSizeCap;
    private Boolean cleanHistoryOnStart;
}
