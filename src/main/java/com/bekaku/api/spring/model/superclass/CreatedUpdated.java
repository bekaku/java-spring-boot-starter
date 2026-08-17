package com.bekaku.api.spring.model.superclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class CreatedUpdated extends Id {

    @JsonIgnore
    @CreatedDate
    @Column(name = "created_date", updatable = false, comment = "Timestamp when this record was created")
    private LocalDateTime createdDate;

    @JsonIgnore
    @LastModifiedDate
    @Column(name = "updated_date", comment = "Timestamp when this record was last modified")
    private LocalDateTime updatedDate;

}
