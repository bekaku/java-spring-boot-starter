package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.RoleDto;
import com.bekaku.api.spring.mapper.RoleMapper;
import com.bekaku.api.spring.model.Role;
import com.bekaku.api.spring.repository.RoleRepository;
import com.bekaku.api.spring.service.RoleService;
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

@Transactional
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<RoleDto> findAllWithPaging(Pageable pageable) {
        Page<Role> result = roleRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<RoleDto> findAllWithSearch(SearchSpecification<Role> specification, Pageable pageable) {
        Page<Role> result = roleRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<RoleDto> findAllBy(Specification<Role> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Role> findAllPageSpecificationBy(Specification<Role> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Role> findAllPageSearchSpecificationBy(SearchSpecification<Role> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<RoleDto> getListFromResult(Page<Role> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role update(Role role) {
        return roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public void delete(Role role) {
        roleRepository.delete(role);
    }

    @Override
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public RoleDto convertEntityToDto(Role role) {
        return modelMapper.toDto(role);
    }

    @Override
    public Role convertDtoToEntity(RoleDto roleDto) {
        return modelMapper.toEntity(roleDto);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }


    @Override
    public Optional<Role> findByNameAndFrontEnd(String name, boolean frontend) {
        return roleRepository.findByNameAndFrontEnd(name, frontend);
    }

    @Override
    public List<Role> findAllByFrontEndOrderByNameAsc(boolean frontEnd) {
        return roleRepository.findAllByFrontEndOrderByNameAsc(frontEnd);
    }



}
