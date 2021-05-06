package io.beka.repository;

import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessTokenRepository extends BaseRepository<AccessToken, Long> {

    Optional<AccessToken> findByToken(String token);

    @Query("SELECT a FROM AccessToken a WHERE a.token =?1 AND a.revoked =?2 ")
    Optional<AccessToken> findAccessTokenByToken(String token, boolean revoked);

    void deleteByToken(String token);

    void deleteByApiClient(ApiClient apiClient);

}
