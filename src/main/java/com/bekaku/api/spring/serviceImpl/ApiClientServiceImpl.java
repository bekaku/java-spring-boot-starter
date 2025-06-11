package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.dto.ApiClientDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.repository.AccessTokenRepository;
import com.bekaku.api.spring.repository.ApiClientRepository;
import com.bekaku.api.spring.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

@Transactional
@RequiredArgsConstructor
@Service
public class ApiClientServiceImpl implements ApiClientService {

    private final ApiClientRepository apiClientRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final ModelMapper modelMapper;

    Logger logger = LoggerFactory.getLogger(ApiClientServiceImpl.class);

    @Transactional(readOnly = true)
    @Override
    public Optional<ApiClient> findByApiName(String apiName) {
        return apiClientRepository.findByApiName(apiName);
    }

    @Override
    public void saveAll(List<ApiClient> apiClientList) {
        apiClientRepository.saveAll(apiClientList);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<ApiClientDto> findAllWithPaging(Pageable pageable) {
        Page<ApiClient> result = apiClientRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public List<ApiClient> findAll() {
        return apiClientRepository.findAll();
    }

    @Override
    public ApiClient save(ApiClient apiClient) {
        return apiClientRepository.save(apiClient);
    }

    @Override
    public ApiClient update(ApiClient apiClient) {
        return apiClientRepository.save(apiClient);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ApiClient> findById(Long id) {
        return apiClientRepository.findById(id);
    }

    @Override
    public void delete(ApiClient apiClient) {
        accessTokenRepository.deleteByApiClient(apiClient);
        apiClientRepository.delete(apiClient);
    }

    @Override
    public void deleteById(Long id) {
        Optional<ApiClient> apiClient = findById(id);
        apiClient.ifPresent(this::delete);
    }

    @Override
    public ApiClientDto convertEntityToDto(ApiClient apiClient) {
        return modelMapper.map(apiClient, ApiClientDto.class);
    }

    @Override
    public ApiClient convertDtoToEntity(ApiClientDto apiClientDto) {
        return modelMapper.map(apiClientDto, ApiClient.class);
    }
}
