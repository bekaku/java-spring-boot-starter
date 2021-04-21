package io.beka.repository;

import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;

import java.util.List;

public interface ApiClientIpRepository extends BaseRepository<ApiClientIp, Long>{

    List<ApiClientIp> findByApiClient(ApiClient apiClient);
}
