package com.bekaku.api.spring.service;

import com.bekaku.api.spring.model.AuditLog;

public interface AuditLogService extends BaseService<AuditLog, AuditLog> {

    void logAction(String action, Object entity);
}
