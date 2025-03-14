package com.bekaku.api.spring.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.AuditLog;

@Repository
public interface AuditLogRepository extends BaseRepository<AuditLog,Long>, JpaSpecificationExecutor<AuditLog> {
}
