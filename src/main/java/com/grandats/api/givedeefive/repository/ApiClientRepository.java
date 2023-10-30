package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.ApiClient;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiClientRepository extends BaseRepository<ApiClient, Long>, JpaSpecificationExecutor<ApiClient> {

    Optional<ApiClient> findByApiName(String apiName);
}
