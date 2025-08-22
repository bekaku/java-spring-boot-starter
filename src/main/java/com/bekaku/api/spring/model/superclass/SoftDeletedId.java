package com.bekaku.api.spring.model.superclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class SoftDeletedId extends Id implements Serializable {
    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean deleted = false;
}
