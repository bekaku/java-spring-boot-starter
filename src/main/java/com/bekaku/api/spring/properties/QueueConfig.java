package com.bekaku.api.spring.properties;

import lombok.Data;

@Data
public class QueueConfig {
    private String key;
    private String name;
    private String name2;
    private String calculateScoreLogsName;
    private String sendNotifyName;
    private int userLevelFrequency;
}
