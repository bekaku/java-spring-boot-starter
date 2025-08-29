package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.AppRoleDto;
import com.bekaku.api.spring.model.AppRole;

import java.util.List;
import java.util.Optional;

public interface AppRoleService extends BaseService<AppRole, AppRoleDto> {

    Optional<AppRole> findByName(String name);

    List<AppRole> findAllByOrderByNameAsc();
}
