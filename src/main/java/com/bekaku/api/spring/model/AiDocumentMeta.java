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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GenSourceableTable(createController = false)
@SQLRestriction("deleted = false")
//@SQLDelete(sql = "UPDATE ai_document_meta SET deleted = true WHERE id = ?")
@Table(name = "ai_document_meta", comment = "Table for storing AI document metadata, vector mappings, and properties.")
@Getter
@Setter
@Entity
public class AiDocumentMeta extends SoftDeletedAuditable<Long> {

    @Column(name = "file_name", length = 255, comment = "Name of the document file")
    private String fileName;

    @Column(name = "active", comment = "Flag indicating whether this document is active for AI processing/retrieval")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_mime", comment = "FK -> Ref table: file_mime (id). MIME type reference for the document")
    private FileMime fileMime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ai_document_vector_ids", joinColumns = @JoinColumn(name = "document_id", comment = "FK -> Ref table: ai_document_meta (id). Document identifier"))
    @Column(name = "vector_id", comment = "Vector ID / Chunk ID stored in the Vector Database")
    private List<String> vectorIds;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "ai_document_metadata",
            joinColumns = @JoinColumn(name = "document_id", comment = "FK -> Ref table: ai_document_meta (id). Document identifier")
    )
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value", comment = "Metadata value content")
    private Map<String, String> metadata = new HashMap<>();

//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(columnDefinition = "jsonb")
//    private Map<String, Object> metadata = new HashMap<>();

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "fileName");
    }
}
