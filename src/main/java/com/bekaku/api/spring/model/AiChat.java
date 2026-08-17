package com.bekaku.api.spring.model;


import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Auditable;
import com.bekaku.api.spring.model.superclass.CreatedUpdated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@GenSourceableTable(createController = false, createPermission = false)
@Table(name = "ai_chat", comment = "Table for storing AI chat sessions and conversation topics.",
        indexes = {
                @Index(columnList = "created_user"),
        })
@Getter
@Setter
@Entity
public class AiChat extends CreatedUpdated {

    @Column(name = "title", length = 255, comment = "Title or topic name of the AI chat session")
    private String title;

    @Column(name = "pin", columnDefinition = "boolean default false", comment = "Flag indicating whether this chat session is pinned to the top")
    private boolean pin;

    @Column(name = "created_user", updatable = false, comment = "Identifier of the user who created this record")
    private Long createdUser;

    @Column(name = "updated_user", comment = "Identifier of the user who last updated this record")
    private Long updatedUser;

    public static Sort getSort() {
        return Sort.by(
                Sort.Order.desc("pin"),
                Sort.Order.desc("updatedDate")
        );
    }
}
