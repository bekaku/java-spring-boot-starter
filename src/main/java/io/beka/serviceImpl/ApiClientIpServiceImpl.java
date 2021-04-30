package io.beka.serviceImpl;

import io.beka.dto.ApiClientIpDto;
import io.beka.vo.Paging;
import io.beka.dto.ResponseListDto;
import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;
import io.beka.repository.ApiClientIpRepository;
import io.beka.service.ApiClientIpService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
public class ApiClientIpServiceImpl implements ApiClientIpService {

    private final ApiClientIpRepository apiClientIpRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseListDto<ApiClientIpDto> findAllWithPaging(Pageable pageable) {
        return null;
    }

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
        return modelMapper.map(apiClientIp, ApiClientIpDto.class);
    }

    @Override
    public ApiClientIp convertDtoToEntity(ApiClientIpDto apiClientIpDto) {
        return modelMapper.map(apiClientIpDto, ApiClientIp.class);
    }


    @Override
    public Optional<ApiClientIp> findByIdAndApiClientId(Long id, Long apiClientId) {
        return apiClientIpRepository.findByIdAndApiClientId(id, apiClientId);
    }

    @Override
    public List<ApiClientIp> findAllByApiClient(ApiClient apiClient) {
        return apiClientIpRepository.findByApiClient(apiClient);
    }

    @Override
    public ResponseListDto<ApiClientIpDto> findPageByApiClient(Long apiCilentId, Pageable pageable) {
        Page<ApiClientIp> result = apiClientIpRepository.findPageByApiClient(apiCilentId, pageable);
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
    }

    @Override
    public Optional<ApiClientIp> findByApiClientIdAndIpAddress(Long apiClientId, String ipAddress) {
        return apiClientIpRepository.findByApiClientIdAndIpAddress(apiClientId, ipAddress);
    }

}
