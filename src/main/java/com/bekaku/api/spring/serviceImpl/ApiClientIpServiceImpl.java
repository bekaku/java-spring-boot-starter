package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ApiClientIpDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.mapper.ApiClientIpMapper;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.ApiClientIp;
import com.bekaku.api.spring.repository.ApiClientIpRepository;
import com.bekaku.api.spring.service.ApiClientIpService;
import com.bekaku.api.spring.specification.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class ApiClientIpServiceImpl implements ApiClientIpService {

    private final ApiClientIpRepository apiClientIpRepository;
    private final ApiClientIpMapper mapper;

    @Override
    public ResponseListDto<ApiClientIpDto> findAllWithPaging(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseListDto<ApiClientIpDto> findAllWithSearch(SearchSpecification<ApiClientIp> specification, Pageable pageable) {
        return null;
    }

    @Override
    public ResponseListDto<ApiClientIpDto> findAllBy(Specification<ApiClientIp> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ApiClientIp> findAllPageSpecificationBy(Specification<ApiClientIp> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ApiClientIp> findAllPageSearchSpecificationBy(SearchSpecification<ApiClientIp> specification, Pageable pageable) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ApiClientIp> findAll() {
        return apiClientIpRepository.findAll();
    }

    @Override
    public ApiClientIp save(ApiClientIp apiClientIp) {
        return apiClientIpRepository.save(apiClientIp);
    }

    @Override
    public ApiClientIp update(ApiClientIp apiClientIp) {
        return apiClientIpRepository.save(apiClientIp);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ApiClientIp> findById(Long id) {
        return apiClientIpRepository.findById(id);
    }

    @Override
    public void delete(ApiClientIp apiClientIp) {
        apiClientIpRepository.delete(apiClientIp);
    }

    @Override
    public void deleteById(Long id) {
        apiClientIpRepository.deleteById(id);
    }

    @Override
    public ApiClientIpDto convertEntityToDto(ApiClientIp apiClientIp) {
        return mapper.toDto(apiClientIp);
    }

    @Override
    public ApiClientIp convertDtoToEntity(ApiClientIpDto apiClientIpDto) {
        return mapper.toEntity(apiClientIpDto);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<ApiClientIp> findByIdAndApiClientId(Long id, Long apiClientId) {
        return apiClientIpRepository.findByIdAndApiClientId(id, apiClientId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ApiClientIp> findAllByApiClient(ApiClient apiClient) {
        return apiClientIpRepository.findByApiClient(apiClient);
    }

    private ResponseListDto<ApiClientIpDto> getListFromResult(Page<ApiClientIp> result){
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<ApiClientIpDto> findPageByApiClient(Long apiCilentId, Pageable pageable) {
        Page<ApiClientIp> result = apiClientIpRepository.findPageByApiClient(apiCilentId, pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<ApiClientIpDto> findPageSearchByApiClient(SearchSpecification<ApiClientIp> specification, Long apiCilentId, Pageable pageable) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ApiClientIp> findByApiClientIdAndIpAddress(Long apiClientId, String ipAddress) {
        return apiClientIpRepository.findByApiClientIdAndIpAddress(apiClientId, ipAddress);
    }

}
