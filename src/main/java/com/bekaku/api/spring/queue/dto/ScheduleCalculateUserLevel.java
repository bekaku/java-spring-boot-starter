package com.bekaku.api.spring.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record ScheduleCalculateUserLevel(
        @JsonProperty("logId") Long logId,
        @JsonProperty("isManual") boolean isManual,
        @JsonProperty("logDate") String logDate,
        @JsonProperty("monthlyLogIds") List<Long> monthlyLogIds
) implements Serializable {
}
