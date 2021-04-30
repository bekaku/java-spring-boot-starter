package io.beka.repository;

import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiClientIpRepository extends BaseRepository<ApiClientIp, Long> {

    List<ApiClientIp> findByApiClient(ApiClient apiClient);

    @Query(value = "SELECT a FROM ApiClientIp a WHERE a.apiClient.id = :apiCilentId")
    Page<ApiClientIp> findPageByApiClient(@Param(value = "apiCilentId") long apiCilentId, Pageable pageable);

    Optional<ApiClientIp> findByIdAndApiClientId(Long id, Long apiClientId);

    Optional<ApiClientIp> findByApiClientIdAndIpAddress(Long apiClientId, String ipAddress);
}
