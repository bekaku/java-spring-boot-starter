package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class DtoId {
//    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
}
