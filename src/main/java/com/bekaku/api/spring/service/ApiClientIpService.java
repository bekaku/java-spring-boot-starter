package com.bekaku.api.spring.service;

import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.dto.ApiClientIpDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.ApiClientIp;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ApiClientIpService extends BaseService<ApiClientIp, ApiClientIpDto> {

    Optional<ApiClientIp> findByIdAndApiClientId(Long id, Long apiClientId);

    List<ApiClientIp> findAllByApiClient(ApiClient apiClient);

    ResponseListDto<ApiClientIpDto> findPageByApiClient(Long apiCilentId, Pageable pageable);

    ResponseListDto<ApiClientIpDto> findPageSearchByApiClient(SearchSpecification<ApiClientIp> specification, Long apiCilentId, Pageable pageable);

    Optional<ApiClientIp> findByApiClientIdAndIpAddress(Long apiClientId, String ipAddress);
}
