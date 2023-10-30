package com.grandats.api.givedeefive.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;

public record ScheduleCalculateDe(
        @JsonProperty("isDaily") boolean isDaily,
        @JsonProperty("isWeekly") boolean isWeekly,
        @JsonProperty("isMonthly") boolean isMonthly,
        @JsonProperty("isQuarter") boolean isQuarter,
        @JsonProperty("isResetPrize") boolean isResetPrize,
        @JsonProperty("calculateOptions") CalculateOptions calculateOptions,
        @JsonProperty("logDate") LocalDate logDate,
        @JsonProperty("userId") Long userId,
        @JsonProperty("organizationId") Long organizationId,
        @JsonProperty("isRankingManage") boolean isRankingManage,
        @JsonProperty("isCalculateUserLevel") boolean isCalculateUserLevel,
        @JsonProperty("calculateUserLevelOptions") ScheduleCalculateUserLevel calculateUserLevelOptions,
        @JsonProperty("companyId") Long companyId

) implements Serializable {
}
