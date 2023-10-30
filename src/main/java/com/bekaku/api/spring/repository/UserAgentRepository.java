package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.UserAgent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAgentRepository extends BaseRepository<UserAgent, Long>, JpaSpecificationExecutor<UserAgent> {
    Optional<UserAgent> findByAgent(String name);
}
