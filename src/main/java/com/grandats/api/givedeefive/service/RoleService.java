package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.RoleDto;
import com.grandats.api.givedeefive.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService extends BaseService<Role, RoleDto> {

    Optional<Role> findByName(String name);

    Optional<Role> findByNameAndFrontEnd(String name, boolean frontend);

    List<Role> findAllByFrontEndOrderByNameAsc(boolean frontEnd);


}
