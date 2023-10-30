package com.bekaku.api.spring.queue.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendNotifyQueue(@JsonProperty("userId") Long userId,
                              @JsonProperty("fromUserId") Long fromUserId,
                              @JsonProperty("functionCode") String functionCode,
                              @JsonProperty("functionId") Long functionId,
                              @JsonProperty("msg") String msg,
                              @JsonProperty("generateMsessage") boolean generateMsessage,
                              @JsonProperty("themeLeader") boolean themeLeader,
                              @JsonProperty("themeOwner") boolean themeOwner,
                              @JsonProperty("themeSubscribe") boolean themeSubscribe,
                              @JsonProperty("postOwner") boolean postOwner,
                              @JsonProperty("postSubscribe") boolean postSubscribe,
                              @JsonProperty("commentOwner") boolean commentOwner,
                              @JsonProperty("commentMention") boolean commentMention,
                              @JsonProperty("notifyForComment") boolean notifyForComment,
                              @JsonProperty("commentId") Long commentId,
                              @JsonProperty("emojiType") String emojiType

) implements Serializable {
}
