package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@JsonRootName("fileUploadChunkMergeRequest")
@Getter
@Setter
public class FileUploadChunkMergeRequestDto {
    @Min(value = 1, message = "{error.min.message}")
    private int totalChunks;

    private String fileMime;
    private String originalFilename;

    @NotEmpty(message = "{error.NotEmpty}")
    private String chunkFilename;

    private Long fileDirectoryId;

    private boolean resizeImage;
}
