package com.bekaku.api.spring.model.superclass;

import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
public class CodeNameSoftDeletedAuditable extends SoftDeletedAuditable<Long> {
    private String code;
    private String name;
}
