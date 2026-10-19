package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.properties.AppCorsProperties;
import com.bekaku.api.spring.util.ConstantData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

//@Configuration(proxyBeanMethods = false)
//@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${environments.production}")
    private boolean production;
    private final AppCorsProperties appCorsProperties;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Set prefixes for the endpoint that the client listens for our messages from
        config.enableSimpleBroker("/topic", "/queue");
        // Set prefix for endpoints the client will send messages toF
        config.setApplicationDestinationPrefixes("/ws");
        config.setUserDestinationPrefix("/users");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ConstantData.WEB_SOCKET_ENDPOINT)
                .setAllowedOrigins(allowedOrigins(appCorsProperties.allowedOrigins(), production));
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketUserInterceptor());
    }
    /** Origins from {@code app.cors.allowed-origins}; {@code "*"} is dropped in production. */
    static String[] allowedOrigins(List<String> configured, boolean production) {
        if (configured == null) {
            return new String[0];
        }
        return configured.stream()
                .filter(origin -> origin != null && !origin.isBlank())
                .filter(origin -> {
                    if (production && origin.contains("*")) {
                        log.warn("Ignoring wildcard WebSocket origin in production: {}", origin);
                        return false;
                    }
                    return true;
                })
                .toArray(String[]::new);
    }
}
