package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.FileMimeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileManagerDto {
    public void assign(Long id, String fileMime, String fileName, String filePath, String fileThumbnailPath,
                       String fileSize, LocalDateTime createdDate,LocalDateTime updatedDate, FileMimeType fileMimeType) {
        this.id = id;
        this.fileMime = fileMime;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileThumbnailPath = fileThumbnailPath;
        this.fileSize = fileSize;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.fileMimeType = fileMimeType;
    }

    private Long id;
    private String fileMime;
    private String fileName;
    private String filePath;
    private String fileThumbnailPath;
    private String fileSize;
    private Long fileSizeNo;
    private Long fileCount;
    private Long functionId;
    private FileMimeType fileMimeType;
    private int duration;
    private String title;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    @JsonIgnore
    private Long ownerId;
    @JsonIgnore
    private Long updatedUserId;
}
