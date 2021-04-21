package io.beka.service;

import io.beka.dto.RoleDto;
import io.beka.model.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface RoleService extends BaseService<Role, RoleDto> {

    Optional<Role> findByName(String name);

}
