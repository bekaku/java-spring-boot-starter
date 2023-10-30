package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.ApiClient;
import com.grandats.api.givedeefive.model.ApiClientIp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiClientIpRepository extends BaseRepository<ApiClientIp, Long>, JpaSpecificationExecutor<ApiClientIp> {

    List<ApiClientIp> findByApiClient(ApiClient apiClient);

    @Query(value = "SELECT a FROM ApiClientIp a WHERE a.apiClient.id = :apiCilentId")
    Page<ApiClientIp> findPageByApiClient(@Param(value = "apiCilentId") long apiCilentId, Pageable pageable);

    Optional<ApiClientIp> findByIdAndApiClientId(Long id, Long apiClientId);

    Optional<ApiClientIp> findByApiClientIdAndIpAddress(Long apiClientId, String ipAddress);
}
