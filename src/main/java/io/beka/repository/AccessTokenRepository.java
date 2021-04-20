package io.beka.repository;

import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessTokenRepository extends BaseRepository<AccessToken, Long> {

    Optional<AccessToken> findByToken(String token);
    
    void deleteByToken(String token);

    void deleteByApiClient(ApiClient apiClient);

}
