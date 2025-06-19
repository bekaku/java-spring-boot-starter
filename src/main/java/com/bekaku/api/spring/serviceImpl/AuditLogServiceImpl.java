package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.AuthenticationHelper;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.AuditLog;
import com.bekaku.api.spring.repository.AuditLogRepository;
import com.bekaku.api.spring.service.AuditLogService;
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
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final AuthenticationHelper authHelper;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AuditLog> findAllWithPaging(Pageable pageable) {
        Page<AuditLog> result = auditLogRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AuditLog> findAllWithSearch(SearchSpecification<AuditLog> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AuditLog> findAllBy(Specification<AuditLog> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AuditLog> findAllPageSpecificationBy(Specification<AuditLog> specification, Pageable pageable) {
        return auditLogRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AuditLog> findAllPageSearchSpecificationBy(SearchSpecification<AuditLog> specification, Pageable pageable) {
        return auditLogRepository.findAll(specification, pageable);
    }

    private ResponseListDto<AuditLog> getListFromResult(Page<AuditLog> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }


    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    @Override
    public AuditLog update(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AuditLog> findById(Long id) {
        return auditLogRepository.findById(id);
    }

    @Override
    public void delete(AuditLog auditLog) {
        auditLogRepository.delete(auditLog);
    }

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
