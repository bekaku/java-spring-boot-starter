package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.AuditLog;
import com.bekaku.api.spring.repository.AuditLogRepository;
import com.bekaku.api.spring.service.AuditLogService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.AuthUtil;
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
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final AuthUtil authHelper;

    @Override
    public ResponseListDto<AuditLog> findAllWithPaging(Pageable pageable) {
        Page<AuditLog> result = auditLogRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<AuditLog> findAllWithSearch(SearchSpecification<AuditLog> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Override
    public ResponseListDto<AuditLog> findAllBy(Specification<AuditLog> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<AuditLog> findAllPageSpecificationBy(Specification<AuditLog> specification, Pageable pageable) {
        return auditLogRepository.findAll(specification, pageable);
    }

    @Override
    public Page<AuditLog> findAllPageSearchSpecificationBy(SearchSpecification<AuditLog> specification, Pageable pageable) {
        return auditLogRepository.findAll(specification, pageable);
    }

    private ResponseListDto<AuditLog> getListFromResult(Page<AuditLog> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }


    @Transactional
    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    @Transactional
    @Override
    public AuditLog update(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    @Override
    public Optional<AuditLog> findById(Long id) {
        return auditLogRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(AuditLog auditLog) {
        auditLogRepository.delete(auditLog);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        auditLogRepository.deleteById(id);
    }

    @Override
    public AuditLog convertEntityToDto(AuditLog auditLog) {
        return auditLog;
    }

    @Override
    public AuditLog convertDtoToEntity(AuditLog auditLog) {
        return auditLog;
    }

    @Transactional
    @Override
    public void logAction(String action, Object entity) {
        Long userId = getUserID();
        String ipAddress = authHelper.getClientIpAddress();
        Long entityId = extractEntityId(entity);
        String entityName = entity.getClass().getSimpleName();
        String details = entity.toString(); // Customize this if needed

        //TODO stream to kafka or rabbitMQ for queue loging server
        auditLogRepository.save(new AuditLog(userId+"", action, entityName, entityId, details, ipAddress));
    }

    private Long getUserID() {
        if (authHelper != null && authHelper.getAuthenticatedUser() != null) {
            return authHelper.getAuthenticatedUser();
        }
        return null;
    }

    private Long extractEntityId(Object entity) {
        try {
            return (Long) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            return null; // If entity has no ID method
        }
    }
}
