package io.beka.service;

import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;

import java.util.List;

public interface ApiClientIpService extends BaseService<ApiClientIp, ApiClientIp> {

    List<ApiClientIp> findByApiClient(ApiClient apiClient);

}
