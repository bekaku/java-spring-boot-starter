package io.beka.repository;

import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import io.beka.model.Permission;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessTokenRepository extends BaseRepository<AccessToken, Long> {

    Optional<AccessToken> findByToken(String token);

    @Query("SELECT a FROM AccessToken a WHERE a.token =:token AND a.revoked =:revoked ")
    Optional<AccessToken> findByToken(@Param(value = "token") String token, @Param(value = "revoked") boolean revoked);
    
    void deleteByToken(String token);

    void deleteByApiClient(ApiClient apiClient);

}
