package com.grandats.api.givedeefive.configuration;

import com.grandats.api.givedeefive.util.ConstantData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//@Configuration
//@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${environments.production}")
    boolean isProduction;

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
        registry.addEndpoint("/_websocket/chatwithuser")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint(ConstantData.WEB_SOCKET_ENDPOINT); // This will allow you to use ws://localhost:8080/test to establish websocket connection
        registry.addEndpoint(ConstantData.WEB_SOCKET_ENDPOINT)
                // Allow the origin http://localhost:63343 to send messages to us. (Base url of the client)
                .setAllowedOriginPatterns(!isProduction ? "*" : "http://localhost:8084", "https://frontendDomain.com")
                // Enable SockJS fallback options This will allow you to use http://localhost:8080/_websocket to establish websocket connection
                .withSockJS()
                .setSessionCookieNeeded(false);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketUserInterceptor());
    }
}
