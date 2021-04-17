package io.beka.service;

import io.beka.dto.ApiClientDto;
import io.beka.model.ApiClient;
import io.beka.repository.AccessTokenRepository;
import io.beka.repository.ApiClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ApiClientService {

    private final ApiClientRepository apiClientRepository;
    private final AccessTokenRepository accessTokenRepository;

    @Transactional(readOnly = true)
    public List<ApiClient> findAll() {
        return apiClientRepository.findAll();
    }

    public ApiClient save(ApiClientDto apiClientDto) {
        return apiClientRepository.save(new ApiClient(apiClientDto.getApiName(), apiClientDto.getByPass(), apiClientDto.getStatus()));
    }

    public Optional<ApiClient> findByApiName(String apiName){
        return apiClientRepository.findByApiName(apiName);
    }

    @Transactional(readOnly = true)
    public Optional<ApiClient> findById(Long id) {
        return apiClientRepository.findById(id);
    }

    public void delete(ApiClient apiClient) {
        accessTokenRepository.deleteByApiClient(apiClient);
        apiClientRepository.delete(apiClient);
    }
}
