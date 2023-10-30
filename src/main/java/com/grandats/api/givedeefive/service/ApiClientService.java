package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.ApiClientDto;
import com.grandats.api.givedeefive.model.ApiClient;

import java.util.List;
import java.util.Optional;

public interface ApiClientService extends BaseService<ApiClient, ApiClientDto> {
    Optional<ApiClient> findByApiName(String apiName);

    void saveAll(List<ApiClient> apiClientList);
}
