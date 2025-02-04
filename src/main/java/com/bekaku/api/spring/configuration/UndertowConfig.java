package com.bekaku.api.spring.configuration;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UndertowConfig {

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowWebSocketDeploymentInfoCustomizer() {
        return factory -> factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            // Create a custom buffer pool
            DefaultByteBufferPool bufferPool = new DefaultByteBufferPool(true, 1024); // true for direct buffers, 1024 buffer size
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(bufferPool); // Set the custom buffer pool
            deploymentInfo.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, webSocketDeploymentInfo);
        });
    }
}