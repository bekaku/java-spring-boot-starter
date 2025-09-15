package com.bekaku.api.spring.properties;

import lombok.Data;

@Data
public class UploadImageConfig {
    private int limitWidth;
    private int limitHeight;
    private boolean createThumbnail;
    private int thumbnailWidth;
    private int maxResolution;
    private String thumbnailExname;
}
