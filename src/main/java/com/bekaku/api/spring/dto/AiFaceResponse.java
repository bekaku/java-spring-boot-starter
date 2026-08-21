package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiFaceResponse(
        boolean success,
        String message,
        @JsonProperty("face_count") int faceCount,
        List<FaceDetail> faces
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FaceDetail(
            List<Integer> bbox,
            @JsonProperty("det_score") double detScore,
            List<Double> embedding,
            @JsonProperty("is_real_face") Boolean isRealFace,
            @JsonProperty("liveness_score") Double livenessScore
    ) {}
}
