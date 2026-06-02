package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.AiDocumentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@JsonRootName("data")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class AiDocumentMetaDto extends DtoId {

    private AiDocumentType documentType;
    private String fileName;
    private boolean isActive;
}
