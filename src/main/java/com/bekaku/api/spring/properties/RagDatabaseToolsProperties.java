package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rag.database-tools")
public record RagDatabaseToolsProperties(
        boolean enabled
) {
}
