package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "audit_log",
        comment = "Table for storing system audit logs and tracking user activities.")
public class AuditLog extends Id {

    @Column(name = "username", comment = "Username of the user who performed the action")
    private String username;

    @Column(name = "action", comment = "Action performed (e.g., CREATE, UPDATE, DELETE)")
    private String action;

    @Column(name = "entity_name", comment = "Name of the entity or domain object affected")
    private String entityName;

    @Column(name = "entity_id", comment = "Identifier of the entity affected")
    private Long entityId;

    // @Column(name = "details", length = 65535)
    @Column(
            name = "details",
            columnDefinition = "TEXT",
            comment = "Detailed description or payload of the audit log"
    )
    private String details;

    @Column(name = "ip_address", comment = "IP address of the client that performed the action")
    private String ipAddress;

    @Column(name = "timestamp", comment = "Timestamp when the audit event occurred")
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
