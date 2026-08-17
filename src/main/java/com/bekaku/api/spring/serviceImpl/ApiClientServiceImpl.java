package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ApiClientDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.mapper.ApiClientMapper;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.repository.AccessTokenRepository;
import com.bekaku.api.spring.repository.ApiClientRepository;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.specification.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ApiClientServiceImpl implements ApiClientService {

    private final ApiClientRepository apiClientRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final ApiClientMapper modelMapper;

    @Override
    public Optional<ApiClient> findByApiName(String apiName) {
        return apiClientRepository.findByApiName(apiName);
    }

    @Transactional
    @Override
    public void saveAll(List<ApiClient> apiClientList) {
        apiClientRepository.saveAll(apiClientList);
    }

    @Override
    public ResponseListDto<ApiClientDto> findAllWithPaging(Pageable pageable) {
        Page<ApiClient> result = apiClientRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<ApiClientDto> findAllWithSearch(SearchSpecification<ApiClient> specification, Pageable pageable) {
        Page<ApiClient> result = apiClientRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<ApiClientDto> findAllBy(Specification<ApiClient> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ApiClient> findAllPageSpecificationBy(Specification<ApiClient> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ApiClient> findAllPageSearchSpecificationBy(SearchSpecification<ApiClient> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<ApiClientDto> getListFromResult(Page<ApiClient> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<ApiClient> findAll() {
        return apiClientRepository.findAll();
    }

    @Transactional
    @Override
    public ApiClient save(ApiClient apiClient) {
        return apiClientRepository.save(apiClient);
    }

    @Transactional
    @Override
    public ApiClient update(ApiClient apiClient) {
        return apiClientRepository.save(apiClient);
    }

    @Override
    public Optional<ApiClient> findById(Long id) {
        return apiClientRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(ApiClient apiClient) {
        accessTokenRepository.deleteByApiClient(apiClient);
        apiClientRepository.delete(apiClient);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Optional<ApiClient> apiClient = findById(id);
        apiClient.ifPresent(this::delete);
    }

    @Override
    public ApiClientDto convertEntityToDto(ApiClient apiClient) {
        return modelMapper.toDto(apiClient);
    }

    @Override
    public ApiClient convertDtoToEntity(ApiClientDto apiClientDto) {
        return modelMapper.toEntity(apiClientDto);
    }
}
