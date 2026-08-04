package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.IdentityLink;

import java.util.List;
import java.util.Optional;

public interface IdentityLinkService extends BaseService<IdentityLink, IdentityLink> {
    Optional<IdentityLink> findByAppUserId(Long appUserId);
    List<IdentityLink> findAllByIdentityGroupIdAndAppUserIdNot(Long groupId, Long appUserId);
    void deleteByAppUserId(Long appUserId);

    void linkAccount(AppUser currentUser, AppUser targetUser);
    void validateSwitchAccount(Long currentUserId, Long targetUserId);
    void removeLinkAccount(Long currentUserId, Long targetUserId);

    List<AppUserDto> getLinkedAccounts(AppUser currentUser);
}
