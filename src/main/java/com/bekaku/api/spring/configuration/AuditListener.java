package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.service.AuditLogService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditListener {
    private static AuditLogService auditLogService;

    @Autowired
    public void setAuthHelper(AuditLogService auditLogService) {
        AuditListener.auditLogService = auditLogService;
    }

    @PrePersist
    public void prePersist(Object entity) {
        auditLogService.logAction("CREATE", entity);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        auditLogService.logAction("UPDATE", entity);
    }

    @PreRemove
    public void preRemove(Object entity) {
        auditLogService.logAction("DELETE", entity);
    }
}
