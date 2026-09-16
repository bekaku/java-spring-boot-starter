package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.ApiClient;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiClientRepository extends BaseRepository<ApiClient, Long>, JpaSpecificationExecutor<ApiClient> {

    Optional<ApiClient> findByApiName(String apiName);
    Optional<ApiClient> findByApiTokenAndStatusIsTrue(String keyHash);

    /**
     * Used by X-API-KEY authentication. Fetches the owning app_user in the same query so a
     * soft-deleted owner resolves to null instead of throwing when a lazy proxy is initialised
     * (AppUser carries @SQLRestriction("deleted=false")).
     */
    @Query("select c from ApiClient c left join fetch c.appUser where c.apiToken = :keyHash and c.status = true")
    Optional<ApiClient> findActiveByApiTokenFetchAppUser(@Param("keyHash") String keyHash);
}
