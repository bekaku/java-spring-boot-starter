package com.bekaku.api.spring.model.superclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class SoftDeleted {
    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean deleted = false;
}
