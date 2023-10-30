package com.bekaku.api.spring.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
