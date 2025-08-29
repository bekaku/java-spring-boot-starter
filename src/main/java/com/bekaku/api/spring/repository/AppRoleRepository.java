package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.AppRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppRoleRepository extends BaseRepository<AppRole, Long>, JpaSpecificationExecutor<AppRole> {

    Optional<AppRole> findByName(String name);

    List<AppRole> findAllByOrderByNameAsc();


}
