package com.bekaku.api.spring.repository;

import java.security.Permission;
import java.util.List;

public interface PermissionRepositoryCustom {
    void softDeleteById(Long id);

    Object[] findById(Long id);

    List<Object[]> findAll();

}
