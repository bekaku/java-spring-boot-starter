package io.beka.vo;

import lombok.Data;

@Data
public class UploadImageConfig {
    private int limitWidth;
    private int limitHeight;
    private boolean createThumbnail;
    private int thumbnailWidth;
    private String thumbnailExname;
}
