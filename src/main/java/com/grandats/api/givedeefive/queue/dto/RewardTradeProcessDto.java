package com.grandats.api.givedeefive.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record RewardTradeProcessDto(
        @JsonProperty("rewardTradeId") Long rewardTradeId
) implements Serializable {
}
