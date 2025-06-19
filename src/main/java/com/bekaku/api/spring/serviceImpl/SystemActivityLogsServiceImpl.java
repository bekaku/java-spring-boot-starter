package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.SystemActivityLogs;
import com.bekaku.api.spring.repository.SystemActivityLogsRepository;
import com.bekaku.api.spring.service.SystemActivityLogsService;
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
@RequiredArgsConstructor
@Service
public class SystemActivityLogsServiceImpl implements SystemActivityLogsService {
    private final SystemActivityLogsRepository systemActivityLogsRepository;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<SystemActivityLogs> findAllWithPaging(Pageable pageable) {
        Page<SystemActivityLogs> result = systemActivityLogsRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<SystemActivityLogs> findAllWithSearch(SearchSpecification<SystemActivityLogs> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<SystemActivityLogs> findAllBy(Specification<SystemActivityLogs> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SystemActivityLogs> findAllPageSpecificationBy(Specification<SystemActivityLogs> specification, Pageable pageable) {
        return systemActivityLogsRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SystemActivityLogs> findAllPageSearchSpecificationBy(SearchSpecification<SystemActivityLogs> specification, Pageable pageable) {
        return systemActivityLogsRepository.findAll(specification, pageable);
    }
    private ResponseListDto<SystemActivityLogs> getListFromResult(Page<SystemActivityLogs> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<SystemActivityLogs> findAll() {
        return systemActivityLogsRepository.findAll();
    }


    public SystemActivityLogs save(SystemActivityLogs systemActivityLogs) {
        return systemActivityLogsRepository.save(systemActivityLogs);
    }

    @Override
    public SystemActivityLogs update(SystemActivityLogs systemActivityLogs) {
        return systemActivityLogsRepository.save(systemActivityLogs);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<SystemActivityLogs> findById(Long id) {
        return systemActivityLogsRepository.findById(id);
    }

    @Override
    public void delete(SystemActivityLogs systemActivityLogs) {
        systemActivityLogsRepository.delete(systemActivityLogs);
    }

    @Override
    public void deleteById(Long id) {
        systemActivityLogsRepository.deleteById(id);
    }

    @Override
    public SystemActivityLogs convertEntityToDto(SystemActivityLogs systemActivityLogs) {
return systemActivityLogs;
    }

    @Override
    public SystemActivityLogs convertDtoToEntity(SystemActivityLogs systemActivityLogs) {
return systemActivityLogs;
    }

}
