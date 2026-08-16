package com.bekaku.api.spring.configuration;


import com.bekaku.api.spring.util.DatabaseSchemaTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiToolConfig {
    @Bean
    public ToolCallback databaseSchemaToolProvider(
            DatabaseSchemaTool databaseSchemaTool
    ) {
        return MethodToolCallback.builder()
                .toolObject(databaseSchemaTool)
                .build();
    }
}
