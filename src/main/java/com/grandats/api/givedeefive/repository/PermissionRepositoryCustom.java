package com.grandats.api.givedeefive.repository;

import java.security.Permission;
import java.util.List;

public interface PermissionRepositoryCustom {
    void softDeleteById(Long id);

    Object[] findById(Long id);

    List<Object[]> findAll();

}
