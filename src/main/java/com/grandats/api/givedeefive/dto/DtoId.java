package com.grandats.api.givedeefive.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class DtoId {
    private Long id;
}
