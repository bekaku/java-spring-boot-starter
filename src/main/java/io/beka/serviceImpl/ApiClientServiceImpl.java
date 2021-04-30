package io.beka.serviceImpl;

import io.beka.controller.test.TestApiController;
import io.beka.dto.ApiClientDto;
import io.beka.dto.ResponseListDto;
import io.beka.model.ApiClient;
import io.beka.repository.AccessTokenRepository;
import io.beka.repository.ApiClientRepository;
import io.beka.service.ApiClientService;
import io.beka.vo.Paging;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<ApiClientDto> findAllWithPaging(Pageable pageable) {
        Page<ApiClient> result = apiClientRepository.findAll(pageable);
        logger.info("findAllWithPaging {} ", result.getContent());

        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
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
