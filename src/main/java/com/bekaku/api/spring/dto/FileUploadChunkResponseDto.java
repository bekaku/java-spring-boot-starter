package com.bekaku.api.spring.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadChunkResponseDto {
    private String filename;
    private String fileMime;
    private boolean status;
    private boolean lastChunk;
}
