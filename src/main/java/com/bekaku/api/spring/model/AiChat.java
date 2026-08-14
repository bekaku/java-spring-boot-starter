package com.bekaku.api.spring.model;


import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Auditable;
import com.bekaku.api.spring.model.superclass.CreatedUpdated;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GenSourceableTable(createController = false, createPermission = false)
@Table(name = "ai_chat", indexes = {
        @Index(columnList = "created_user"),
})
@Getter
@Setter
@Entity
public class AiChat extends Auditable<Long> {

    @Column(name = "title", length = 255)
    private String title;

    @Column(columnDefinition = "boolean default false")
    private boolean pin;

    public static Sort getSort() {
        return Sort.by(
                Sort.Order.desc("pin"),
                Sort.Order.desc("updatedDate")
        );
    }
}
