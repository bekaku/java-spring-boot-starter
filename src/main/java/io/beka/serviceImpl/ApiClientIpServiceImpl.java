package io.beka.serviceImpl;

import io.beka.dto.Paging;
import io.beka.dto.ResponseListDto;
import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;
import io.beka.repository.ApiClientIpRepository;
import io.beka.service.ApiClientIpService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@AllArgsConstructor
@Service
public class ApiClientIpServiceImpl implements ApiClientIpService {

    private final ApiClientIpRepository apiClientIpRepository;

    @Override
    public ResponseListDto<ApiClientIp> findAllWithPaging(Paging paging, Sort sort) {
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
    public ApiClientIp convertEntityToDto(ApiClientIp apiClientIp) {
        return null;
    }

    @Override
    public ApiClientIp convertDtoToEntity(ApiClientIp apiClientIp) {
        return null;
    }

    @Override
    public List<ApiClientIp> findByApiClient(ApiClient apiClient) {
        return apiClientIpRepository.findByApiClient(apiClient);
    }
}
