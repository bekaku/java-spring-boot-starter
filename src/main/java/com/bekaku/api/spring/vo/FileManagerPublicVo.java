package com.bekaku.api.spring.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class FileManagerPublicVo {
    private Long id;
    private String fileMime;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private boolean directoryFolder;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @JsonIgnore
    private Long ownerId;
    @JsonIgnore
    private Long updatedUserId;
}
