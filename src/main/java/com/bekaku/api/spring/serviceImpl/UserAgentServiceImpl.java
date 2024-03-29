package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.UserAgent;
import com.bekaku.api.spring.repository.UserAgentRepository;
import com.bekaku.api.spring.service.UserAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserAgentServiceImpl implements UserAgentService {

    private final UserAgentRepository userAgentRepository;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserAgent> findAllWithPaging(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseListDto<UserAgent> findAllWithSearch(SearchSpecification<UserAgent> specification, Pageable pageable) {
        return null;
    }

    @Override
    public ResponseListDto<UserAgent> findAllBy(Specification<UserAgent> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<UserAgent> findAllPageSpecificationBy(Specification<UserAgent> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<UserAgent> findAllPageSearchSpecificationBy(SearchSpecification<UserAgent> specification, Pageable pageable) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserAgent> findAll() {
        return userAgentRepository.findAll();
    }

    @Override
    public UserAgent save(UserAgent userAgent) {
        return userAgentRepository.save(userAgent);
    }

    @Override
    public UserAgent update(UserAgent userAgent) {
        return userAgentRepository.save(userAgent);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserAgent> findById(Long id) {
        return userAgentRepository.findById(id);
    }

    @Override
    public void delete(UserAgent userAgent) {
        userAgentRepository.delete(userAgent);
    }

    @Override
    public void deleteById(Long id) {
        userAgentRepository.deleteById(id);
    }

    @Override
    public UserAgent convertEntityToDto(UserAgent userAgent) {
        return null;
    }

    @Override
    public UserAgent convertDtoToEntity(UserAgent userAgent) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserAgent> findByAgent(String name) {
        return userAgentRepository.findByAgent(name);
    }
}
