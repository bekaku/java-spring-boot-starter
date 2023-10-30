package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByName(String name);

    List<Role> findAllByFrontEndOrderByNameAsc(boolean frontEnd);


    Optional<Role> findByNameAndFrontEnd(String name, boolean frontend);

}
