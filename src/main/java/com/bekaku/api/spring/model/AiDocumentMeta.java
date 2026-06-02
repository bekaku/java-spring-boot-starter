package com.bekaku.api.spring.model;


import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.enumtype.AiDocumentType;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Sort;

import java.util.List;

@GenSourceableTable()
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE ai_document_meta SET deleted = true WHERE id = ?")
@Table(name = "ai_document_meta")
@Getter
@Setter
@Entity
public class AiDocumentMeta extends SoftDeletedAuditable<Long> {

    private String fileName;
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private AiDocumentType documentType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ai_document_vector_ids", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "vector_id")
    private List<String> vectorIds;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "fileName");
    }
}
