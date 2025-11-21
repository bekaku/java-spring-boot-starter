package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.dto.PermissionDto;

import java.security.Permission;
import java.util.List;
import java.util.Optional;

public interface PermissionRepositoryCustom {
    void softDeleteById(Long id);

    Object[] findById(Long id);

    List<Object[]> findAll();

    Optional<PermissionDto> findDtoBy(Long id);

}
