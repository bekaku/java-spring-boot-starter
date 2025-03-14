package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.configuration.AuditListener;
import com.bekaku.api.spring.model.superclass.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@GenSourceableTable(createPermission = false, createController = false, createDto = false)
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "audit_log")
public class AuditLog extends Id {

    private String username;

    private String action;

    private String entityName;

    private Long entityId;

    @Column(name = "details", columnDefinition = "text default null")
    private String details;

    private String ipAddress;

    private LocalDateTime timestamp;

    public AuditLog(String username, String action, String entityName, Long entityId, String details, String ipAddress) {
        this.username = username;
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.details = details;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }
}
