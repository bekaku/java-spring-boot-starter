package com.bekaku.api.spring.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.IdentityGroup;

@Repository
public interface IdentityGroupRepository extends BaseRepository<IdentityGroup,Long>, JpaSpecificationExecutor<IdentityGroup> {
}
