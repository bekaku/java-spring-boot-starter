package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.ApiClientDto;
import com.bekaku.api.spring.dto.GeneratedApiKey;
import com.bekaku.api.spring.model.ApiClient;

import java.util.List;
import java.util.Optional;

public interface ApiClientService extends BaseService<ApiClient, ApiClientDto> {
    Optional<ApiClient> findByApiTokenAndStatusIsTrue(String keyHash);
    Optional<ApiClient> findByApiName(String apiName);
    void saveAll(List<ApiClient> apiClientList);
    GeneratedApiKey generateKey(String prefix, int byteLength);
    boolean verifyKey(String rawKey, String storedHash);
}
