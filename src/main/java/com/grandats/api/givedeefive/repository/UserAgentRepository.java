package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.UserAgent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAgentRepository extends BaseRepository<UserAgent, Long>, JpaSpecificationExecutor<UserAgent> {
    Optional<UserAgent> findByAgent(String name);
}
