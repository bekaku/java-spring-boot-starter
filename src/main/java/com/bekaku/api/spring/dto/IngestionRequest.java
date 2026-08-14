package com.bekaku.api.spring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngestionRequest {
    @NotBlank(message = "mergedFilePath is required — use the value returned by the chunk upload API")
    private String mergedFilePath;

    @NotBlank(message = "fileName is required")
    private String fileName;
}
