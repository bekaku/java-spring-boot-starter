package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.RoleDto;
import com.bekaku.api.spring.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService extends BaseService<Role, RoleDto> {

    Optional<Role> findByName(String name);

    Optional<Role> findByNameAndFrontEnd(String name, boolean frontend);

    List<Role> findAllByFrontEndOrderByNameAsc(boolean frontEnd);


}
