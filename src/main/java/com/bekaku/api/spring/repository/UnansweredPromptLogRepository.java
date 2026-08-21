package com.bekaku.api.spring.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.UnansweredPromptLog;

@Repository
public interface UnansweredPromptLogRepository extends BaseRepository<UnansweredPromptLog,Long>, JpaSpecificationExecutor<UnansweredPromptLog> {
}
