package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;

public class FaceRecognitionDtos {
    public record RegisterRequest(
            @NotNull(message = "User ID is required") Long userId,
            @NotNull(message = "File ID is required") Long fileManagerId
    ) {}

    public record RegisterResponse(
            @JsonFormat(shape = JsonFormat.Shape.STRING) Long id,
            @JsonFormat(shape = JsonFormat.Shape.STRING) Long userId,
            LocalDateTime createdDate
    ) {}

    public record DetechResponse(
            @JsonFormat(shape = JsonFormat.Shape.STRING) Long appUserId,
            String email,
            String username,
            double similarityScore,
            double cosineDistance,
            ImageDto image,
            LocalDateTime detectionTime,
            String status
    ) {}

    public record FaceMatchProjection(
            Long appUserId,
            String email,
            String username,
            Long fileManagerId,
            String filePath,
            String fileMimeName,
            Double distance
    ) {}
}
