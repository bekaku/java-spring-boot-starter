package io.beka.repository;

import io.beka.model.UserAgent;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAgentRepository extends BaseRepository<UserAgent, Long> {
    Optional<UserAgent> findByAgent(String name);
}
