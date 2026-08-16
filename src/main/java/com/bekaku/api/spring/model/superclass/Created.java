package com.bekaku.api.spring.model.superclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PROTECTED)
@MappedSuperclass
public class Created extends Id {

    @JsonIgnore
    @CreatedDate
    @Column(name = "created_date", updatable = false, comment = "Timestamp when this record was created")
    private LocalDateTime createdDate;

}
