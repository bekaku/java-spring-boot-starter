package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.AppRoleDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.mapper.RoleMapper;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.repository.AppRoleRepository;
import com.bekaku.api.spring.service.AppRoleService;
import com.bekaku.api.spring.specification.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AppRoleServiceImpl implements AppRoleService {
    private final AppRoleRepository appRoleRepository;
    private final RoleMapper modelMapper;

    @Override
    public ResponseListDto<AppRoleDto> findAllWithPaging(Pageable pageable) {
        Page<AppRole> result = appRoleRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<AppRoleDto> findAllWithSearch(SearchSpecification<AppRole> specification, Pageable pageable) {
        Page<AppRole> result = appRoleRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<AppRoleDto> findAllBy(Specification<AppRole> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<AppRole> findAllPageSpecificationBy(Specification<AppRole> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<AppRole> findAllPageSearchSpecificationBy(SearchSpecification<AppRole> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<AppRoleDto> getListFromResult(Page<AppRole> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<AppRole> findAll() {
        return appRoleRepository.findAll();
    }

    @Transactional
    @Override
    public AppRole save(AppRole appRole) {
        return appRoleRepository.save(appRole);
    }

    @Transactional
    @Override
    public AppRole update(AppRole appRole) {
        return appRoleRepository.save(appRole);
    }

    @Override
    public Optional<AppRole> findById(Long id) {
        return appRoleRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(AppRole appRole) {
        appRoleRepository.delete(appRole);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        appRoleRepository.deleteById(id);
    }

    @Override
    public AppRoleDto convertEntityToDto(AppRole appRole) {
        return modelMapper.toDto(appRole);
    }

    @Override
    public AppRole convertDtoToEntity(AppRoleDto appRoleDto) {
        return modelMapper.toEntity(appRoleDto);
    }

    @Override
    public Optional<AppRole> findByName(String name) {
        return appRoleRepository.findByName(name);
    }


    @Override
    public List<AppRole> findAllByOrderByNameAsc() {
        return appRoleRepository.findAllByOrderByNameAsc();
    }



}
