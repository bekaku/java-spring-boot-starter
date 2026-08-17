package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.IdentityGroup;
import com.bekaku.api.spring.repository.IdentityGroupRepository;
import com.bekaku.api.spring.service.IdentityGroupService;
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
public class IdentityGroupServiceImpl implements IdentityGroupService {
    private final IdentityGroupRepository identityGroupRepository;

    @Override
    public ResponseListDto<IdentityGroup> findAllWithPaging(Pageable pageable) {
        Page<IdentityGroup> result = identityGroupRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<IdentityGroup> findAllWithSearch(SearchSpecification<IdentityGroup> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Override
    public ResponseListDto<IdentityGroup> findAllBy(Specification<IdentityGroup> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<IdentityGroup> findAllPageSpecificationBy(Specification<IdentityGroup> specification, Pageable pageable) {
        return identityGroupRepository.findAll(specification, pageable);
    }

    @Override
    public Page<IdentityGroup> findAllPageSearchSpecificationBy(SearchSpecification<IdentityGroup> specification, Pageable pageable) {
        return identityGroupRepository.findAll(specification, pageable);
    }
    private ResponseListDto<IdentityGroup> getListFromResult(Page<IdentityGroup> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<IdentityGroup> findAll() {
        return identityGroupRepository.findAll();
    }

    @Transactional
    public IdentityGroup save(IdentityGroup identityGroup) {
        return identityGroupRepository.save(identityGroup);
    }

    @Transactional
    @Override
    public IdentityGroup update(IdentityGroup identityGroup) {
        return identityGroupRepository.save(identityGroup);
    }

    @Override
    public Optional<IdentityGroup> findById(Long id) {
        return identityGroupRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(IdentityGroup identityGroup) {
        identityGroupRepository.delete(identityGroup);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        identityGroupRepository.deleteById(id);
    }

    @Override
    public IdentityGroup convertEntityToDto(IdentityGroup identityGroup) {
return identityGroup;
    }

    @Override
    public IdentityGroup convertDtoToEntity(IdentityGroup identityGroup) {
return identityGroup;
    }

}
