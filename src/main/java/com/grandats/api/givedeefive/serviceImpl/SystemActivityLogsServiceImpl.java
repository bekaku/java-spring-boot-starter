package com.grandats.api.givedeefive.serviceImpl;

import com.grandats.api.givedeefive.dto.ResponseListDto;
import com.grandats.api.givedeefive.repository.SystemActivityLogsRepository;
import com.grandats.api.givedeefive.specification.SearchSpecification;
import com.grandats.api.givedeefive.model.SystemActivityLogs;
import com.grandats.api.givedeefive.service.SystemActivityLogsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
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
    private final ModelMapper modelMapper;

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
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
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
