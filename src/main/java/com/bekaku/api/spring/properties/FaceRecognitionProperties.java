package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.face-recognition")
public record FaceRecognitionProperties(
        String baseUrl,
        double matchThreshold,
        double minDetectionScore
) {
}
