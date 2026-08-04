package com.bekaku.api.spring.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ImageDto {

    public ImageDto(Long fileSize, Long width, Long height, String mimeType) {
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
        this.mimeType = mimeType;
    }

    private String image;
    private String thumbnail;
    private Long fileSize;
    private Long width;
    private Long height;
    private String mimeType;
}
