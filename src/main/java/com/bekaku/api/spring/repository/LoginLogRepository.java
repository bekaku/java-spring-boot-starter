package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.LoginLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoginLogRepository extends BaseRepository<LoginLog, Long>, JpaSpecificationExecutor<LoginLog> {
}
