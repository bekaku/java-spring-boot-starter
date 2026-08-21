package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class DtoId {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
}
