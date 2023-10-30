package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.LoginLog;
import com.bekaku.api.spring.repository.LoginLogRepository;
import com.bekaku.api.spring.service.LoginLogService;
import com.bekaku.api.spring.specification.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogRepository loginLogRepository;

    @Override
    public ResponseListDto<LoginLog> findAllWithPaging(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseListDto<LoginLog> findAllWithSearch(SearchSpecification<LoginLog> specification, Pageable pageable) {
        return null;
    }

    @Override
    public ResponseListDto<LoginLog> findAllBy(Specification<LoginLog> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<LoginLog> findAllPageSpecificationBy(Specification<LoginLog> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<LoginLog> findAllPageSearchSpecificationBy(SearchSpecification<LoginLog> specification, Pageable pageable) {
        return null;
    }

    @Override
    public List<LoginLog> findAll() {
        return loginLogRepository.findAll();
    }

    @Override
    public LoginLog save(LoginLog loginLog) {
        return loginLogRepository.save(loginLog);
    }

    @Override
    public LoginLog update(LoginLog loginLog) {
        return loginLogRepository.save(loginLog);
    }

    @Override
    public Optional<LoginLog> findById(Long id) {
        return loginLogRepository.findById(id);
    }

    @Override
    public void delete(LoginLog loginLog) {
        loginLogRepository.delete(loginLog);
    }

    @Override
    public void deleteById(Long id) {
        loginLogRepository.deleteById(id);
    }

    @Override
    public LoginLog convertEntityToDto(LoginLog loginLog) {
        return null;
    }

    @Override
    public LoginLog convertDtoToEntity(LoginLog loginLog) {
        return null;
    }
}
