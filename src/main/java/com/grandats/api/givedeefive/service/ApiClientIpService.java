package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.ApiClientIpDto;
import com.grandats.api.givedeefive.dto.ResponseListDto;
import com.grandats.api.givedeefive.specification.SearchSpecification;
import com.grandats.api.givedeefive.model.ApiClient;
import com.grandats.api.givedeefive.model.ApiClientIp;
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
