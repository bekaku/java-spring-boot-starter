package io.beka.service;

import io.beka.dto.ApiClientIpDto;
import io.beka.dto.ResponseListDto;
import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;
import io.beka.vo.Paging;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ApiClientIpService extends BaseService<ApiClientIp, ApiClientIpDto> {

    Optional<ApiClientIp> findByIdAndApiClientId(Long id, Long apiClientId);

    List<ApiClientIp> findAllByApiClient(ApiClient apiClient);

    ResponseListDto<ApiClientIpDto> findPageByApiClient(Long apiCilentId, Pageable pageable);

    Optional<ApiClientIp> findByApiClientIdAndIpAddress(Long apiClientId, String ipAddress);
}
