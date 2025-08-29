package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileManagerDto {
    public void assign(Long id, String fileMime, String fileName, String filePath, String fileThumbnailPath, String fileSize, LocalDateTime createdDate, boolean directoryFolder) {
        this.id = id;
        this.fileMime = fileMime;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileThumbnailPath = fileThumbnailPath;
        this.fileSize = fileSize;
        this.createdDate = createdDate;
        this.directoryFolder = directoryFolder;
    }

    private Long id;
    private String fileMime;
    private String fileName;
    private String filePath;
    private String fileThumbnailPath;
    private String fileSize;
    private Long functionId;
    private boolean directoryFolder;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
