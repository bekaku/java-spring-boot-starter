package com.bekaku.api.spring.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationCount {
    private int totalNotify;
    private Long lastestId;
    private long totalNewMessage;
}
