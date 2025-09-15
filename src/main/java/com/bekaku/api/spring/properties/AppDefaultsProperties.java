package com.bekaku.api.spring.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.defaults")
public class AppDefaultsProperties {
    private Long role;//app.defaults.role
    private String userpwd;//app.defaults.userpwd
    private Long dataStreamChunkSize;//app.defaults.data-stream-chunk-size
    private Long dataStreamLimit;//app.defaults.data-stream-limit
}
