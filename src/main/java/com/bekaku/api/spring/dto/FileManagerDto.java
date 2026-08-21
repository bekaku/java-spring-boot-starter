package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.FileMimeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class FileManagerDto extends  DtoId {
    public void assign(Long id, String fileMime, String fileName, String filePath, String fileThumbnailPath,
                       String fileSize, LocalDateTime createdDate, LocalDateTime updatedDate, FileMimeType fileMimeType, String streamPath) {
        this.setId(id);
        this.fileMime = fileMime;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileThumbnailPath = fileThumbnailPath;
        this.fileSize = fileSize;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.fileMimeType = fileMimeType;
        this.streamPath = streamPath;
    }

    private String fileMime;
    private String fileName;
    private String filePath;
    private String fileThumbnailPath;
    private String fileSize;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fileSizeNo;
    private Long fileCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long functionId;
    private FileMimeType fileMimeType;
    private int duration;
    private String title;
    private String description;
    private boolean useThumbnail;
    private String uniqueId;
    private String streamPath;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @JsonIgnore
    private Long ownerId;
    @JsonIgnore
    private Long updatedUserId;
}
