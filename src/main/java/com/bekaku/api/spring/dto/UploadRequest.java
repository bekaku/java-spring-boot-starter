package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonRootName("uploadRequest")
@Data
@NoArgsConstructor
public class UploadRequest {
    private String fileBase64;
    private Long fileDirectoryId;
    private boolean resizeImage;
}
