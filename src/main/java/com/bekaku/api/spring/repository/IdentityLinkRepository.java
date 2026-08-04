package com.bekaku.api.spring.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.IdentityLink;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityLinkRepository extends BaseRepository<IdentityLink,Long>, JpaSpecificationExecutor<IdentityLink> {
    Optional<IdentityLink> findByAppUserId(Long appUserId);
    List<IdentityLink> findAllByIdentityGroupIdAndAppUserIdNot(Long groupId, Long appUserId);
    void deleteByAppUserId(Long appUserId);
    List<IdentityLink> findByIdentityGroupId(Long groupId);
}
