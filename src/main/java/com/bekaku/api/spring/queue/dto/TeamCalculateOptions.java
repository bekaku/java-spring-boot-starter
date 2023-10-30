package com.bekaku.api.spring.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record TeamCalculateOptions(
        @JsonProperty("thirtyPastDailyLogId") Long thirtyPastDailyLogId,
        @JsonProperty("thirtyPastWeeklyLogId") Long thirtyPastWeeklyLogId,
        @JsonProperty("monthlyLogId") Long monthlyLogId

) implements Serializable {
}
