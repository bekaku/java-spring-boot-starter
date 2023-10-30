package com.grandats.api.givedeefive.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.grandats.api.givedeefive.model.SystemActivityLogs;

@Repository
public interface SystemActivityLogsRepository extends BaseRepository<SystemActivityLogs,Long>, JpaSpecificationExecutor<SystemActivityLogs> {
}
