package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.UnansweredPromptLog;
import com.bekaku.api.spring.repository.UnansweredPromptLogRepository;
import com.bekaku.api.spring.service.UnansweredPromptLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UnansweredPromptLogServiceImpl implements UnansweredPromptLogService {
    private final UnansweredPromptLogRepository unansweredPromptLogRepository;

    @Override
    public ResponseListDto<UnansweredPromptLog> findAllWithPaging(Pageable pageable) {
        Page<UnansweredPromptLog> result = unansweredPromptLogRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<UnansweredPromptLog> findAllWithSearch(SearchSpecification<UnansweredPromptLog> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Override
    public ResponseListDto<UnansweredPromptLog> findAllBy(Specification<UnansweredPromptLog> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<UnansweredPromptLog> findAllPageSpecificationBy(Specification<UnansweredPromptLog> specification, Pageable pageable) {
        return unansweredPromptLogRepository.findAll(specification, pageable);
    }

    @Override
    public Page<UnansweredPromptLog> findAllPageSearchSpecificationBy(SearchSpecification<UnansweredPromptLog> specification, Pageable pageable) {
        return unansweredPromptLogRepository.findAll(specification, pageable);
    }

    private ResponseListDto<UnansweredPromptLog> getListFromResult(Page<UnansweredPromptLog> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<UnansweredPromptLog> findAll() {
        return unansweredPromptLogRepository.findAll();
    }


    @Transactional
    public UnansweredPromptLog save(UnansweredPromptLog unansweredPromptLog) {
        return unansweredPromptLogRepository.save(unansweredPromptLog);
    }

    @Transactional
    @Override
    public UnansweredPromptLog update(UnansweredPromptLog unansweredPromptLog) {
        return unansweredPromptLogRepository.save(unansweredPromptLog);
    }

    @Override
    public Optional<UnansweredPromptLog> findById(Long id) {
        return unansweredPromptLogRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(UnansweredPromptLog unansweredPromptLog) {
        unansweredPromptLogRepository.delete(unansweredPromptLog);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        unansweredPromptLogRepository.deleteById(id);
    }

    @Override
    public UnansweredPromptLog convertEntityToDto(UnansweredPromptLog unansweredPromptLog) {
        return unansweredPromptLog;
    }

    @Override
    public UnansweredPromptLog convertDtoToEntity(UnansweredPromptLog unansweredPromptLog) {
        return unansweredPromptLog;
    }

    @Transactional
    public void logUnansweredPrompt(Long userId, String prompt) {
        UnansweredPromptLog log = new UnansweredPromptLog();
        log.setUserId(userId);
        log.setPrompt(prompt);
        log.setAcknowledged(false);
        unansweredPromptLogRepository.save(log);
    }

}
