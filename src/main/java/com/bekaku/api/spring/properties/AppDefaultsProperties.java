package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.defaults")
public record AppDefaultsProperties(
        Long role,//app.defaults.role
        String userpwd,//app.defaults.userpwd
        Long dataStreamChunkSize,//app.defaults.data-stream-chunk-size
        Long dataStreamLimit//app.defaults.data-stream-limit
) {

}
