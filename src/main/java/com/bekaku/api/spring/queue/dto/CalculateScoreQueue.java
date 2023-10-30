package com.bekaku.api.spring.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record CalculateScoreQueue(@JsonProperty("postOrCommentDataId") Long postOrCommentDataId,
                                  @JsonProperty("userId") Long userId,
                                  @JsonProperty("scoreLogType") String scoreLogType,
                                  @JsonProperty("options") CalculateScoreOptions options
) implements Serializable {
}
