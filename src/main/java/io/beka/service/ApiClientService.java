package io.beka.service;

import io.beka.dto.ApiClientDto;
import io.beka.model.ApiClient;

import java.util.Optional;

public interface ApiClientService extends BaseService<ApiClient, ApiClientDto> {
    Optional<ApiClient> findByApiName(String apiName);
}
