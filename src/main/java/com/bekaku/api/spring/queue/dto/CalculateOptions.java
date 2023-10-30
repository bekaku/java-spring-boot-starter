package com.bekaku.api.spring.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record CalculateOptions(
        @JsonProperty("thirtyPastDailyLogId") Long thirtyPastDailyLogId,
        @JsonProperty("thirtyPastWeeklyLogId") Long thirtyPastWeeklyLogId,
        @JsonProperty("monthlyLogId") Long monthlyLogId,
        @JsonProperty("monthlyLogIds") List<Long> monthlyLogIds,
        @JsonProperty("quarterLogId") Long quarterLogId
        ) implements Serializable {
}
