package com.bekaku.api.spring.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.SystemActivityLogs;

@Repository
public interface SystemActivityLogsRepository extends BaseRepository<SystemActivityLogs,Long>, JpaSpecificationExecutor<SystemActivityLogs> {
}
