package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.Role;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByName(String name);

    List<Role> findAllByFrontEndOrderByNameAsc(boolean frontEnd);


    Optional<Role> findByNameAndFrontEnd(String name, boolean frontend);

}
