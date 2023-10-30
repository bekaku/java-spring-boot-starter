package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.LoginLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoginLogRepository extends BaseRepository<LoginLog, Long>, JpaSpecificationExecutor<LoginLog> {
}
