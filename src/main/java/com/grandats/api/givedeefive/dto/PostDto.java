package com.grandats.api.givedeefive.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public interface PostDto {
    Long getId();

    String getPostContent();

    String getUserEmail();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getPostDatetime();
}
