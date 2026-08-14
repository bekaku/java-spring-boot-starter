package com.bekaku.api.spring.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class IngestionResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String fileName;
    private String fileMime;
    private int chunkCount;
    private String message;
}
