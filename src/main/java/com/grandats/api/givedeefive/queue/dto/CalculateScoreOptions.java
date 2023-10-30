package com.grandats.api.givedeefive.queue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record CalculateScoreOptions(
        @JsonProperty("weightScoreHalfForCommentGive") boolean weightScoreHalfForCommentGive,
        @JsonProperty("updateCountingForPostData") boolean updateCountingForPostData,
        @JsonProperty("scoreToPost") boolean scoreToPost,
        @JsonProperty("countingScore") boolean countingScore,
        @JsonProperty("theSameDateAction") boolean theSameDateAction

) implements Serializable {
}
