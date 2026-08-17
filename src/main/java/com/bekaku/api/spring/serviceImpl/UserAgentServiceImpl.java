package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.UserAgent;
import com.bekaku.api.spring.repository.UserAgentRepository;
import com.bekaku.api.spring.service.UserAgentService;
import com.bekaku.api.spring.specification.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserAgentServiceImpl implements UserAgentService {

    private final UserAgentRepository userAgentRepository;

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

    @Override
    public List<UserAgent> findAll() {
        return userAgentRepository.findAll();
    }

    @Override
    @Transactional
    public UserAgent save(UserAgent userAgent) {
        return userAgentRepository.save(userAgent);
    }

    @Override
    @Transactional
    public UserAgent update(UserAgent userAgent) {
        return userAgentRepository.save(userAgent);
    }

    @Override
    public Optional<UserAgent> findById(Long id) {
        return userAgentRepository.findById(id);
    }

    @Override
    @Transactional
    public void delete(UserAgent userAgent) {
        userAgentRepository.delete(userAgent);
    }

    @Override
    @Transactional
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

    @Override
    public Optional<UserAgent> findByAgent(String name) {
        return userAgentRepository.findByAgent(name);
    }
}
