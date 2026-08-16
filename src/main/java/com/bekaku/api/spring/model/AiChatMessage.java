package com.bekaku.api.spring.model;


import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.enumtype.AiRole;
import com.bekaku.api.spring.model.superclass.Auditable;
import com.bekaku.api.spring.model.superclass.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@GenSourceableTable(createController = false, createPermission = false)
@Table(name = "ai_chat_messages", comment = "Table for storing individual messages within AI chat sessions.")
@Getter
@Setter
@Entity
public class AiChatMessage extends Id {

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50, nullable = false, comment = "Role of the message sender (e.g., USER, ASSISTANT, SYSTEM)")
    private AiRole aiRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_chat", nullable = false, comment = "FK -> Ref table: ai_chat (id). The parent AI chat session")
    @ToString.Exclude
    private AiChat aiChat;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false, comment = "Content payload of the message (user prompt or AI response)")
    private String content;

    @CreatedDate
    @Column(name = "created_date", updatable = false, comment = "Timestamp when the message was sent/created")
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "id");
    }
}
