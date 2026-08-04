package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.LoginRequest;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.IdentityGroup;
import com.bekaku.api.spring.model.IdentityLink;
import com.bekaku.api.spring.repository.IdentityGroupRepository;
import com.bekaku.api.spring.repository.IdentityLinkRepository;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.IdentityLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class IdentityLinkServiceImpl implements IdentityLinkService {
    private final IdentityLinkRepository identityLinkRepository;
    private final IdentityGroupRepository identityGroupRepository;
    private final AppUserService appUserService;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<IdentityLink> findAllWithPaging(Pageable pageable) {
        Page<IdentityLink> result = identityLinkRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<IdentityLink> findAllWithSearch(SearchSpecification<IdentityLink> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<IdentityLink> findAllBy(Specification<IdentityLink> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<IdentityLink> findAllPageSpecificationBy(Specification<IdentityLink> specification, Pageable pageable) {
        return identityLinkRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<IdentityLink> findAllPageSearchSpecificationBy(SearchSpecification<IdentityLink> specification, Pageable pageable) {
        return identityLinkRepository.findAll(specification, pageable);
    }

    private ResponseListDto<IdentityLink> getListFromResult(Page<IdentityLink> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<IdentityLink> findAll() {
        return identityLinkRepository.findAll();
    }


    public IdentityLink save(IdentityLink identityLink) {
        return identityLinkRepository.save(identityLink);
    }

    @Override
    public IdentityLink update(IdentityLink identityLink) {
        return identityLinkRepository.save(identityLink);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<IdentityLink> findById(Long id) {
        return identityLinkRepository.findById(id);
    }

    @Override
    public void delete(IdentityLink identityLink) {
        identityLinkRepository.delete(identityLink);
    }

    @Override
    public void deleteById(Long id) {
        identityLinkRepository.deleteById(id);
    }

    @Override
    public IdentityLink convertEntityToDto(IdentityLink identityLink) {
        return identityLink;
    }

    @Override
    public IdentityLink convertDtoToEntity(IdentityLink identityLink) {
        return identityLink;
    }

    @Override
    public Optional<IdentityLink> findByAppUserId(Long appUserId) {
        return identityLinkRepository.findByAppUserId(appUserId);
    }

    @Override
    public List<IdentityLink> findAllByIdentityGroupIdAndAppUserIdNot(Long groupId, Long appUserId) {
        return identityLinkRepository.findAllByIdentityGroupIdAndAppUserIdNot(groupId, appUserId);
    }

    @Override
    public void deleteByAppUserId(Long appUserId) {
        identityLinkRepository.deleteByAppUserId(appUserId);
    }

    @Transactional
    public void linkAccount(AppUser currentUser, AppUser targetUser) {

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("Unable to link the account to myself.");
        }

        Optional<IdentityLink> currentUserLinkOpt = identityLinkRepository.findByAppUserId(currentUser.getId());
        IdentityGroup group;

        if (currentUserLinkOpt.isEmpty()) {
            group = new IdentityGroup();
            group = identityGroupRepository.save(group);

            IdentityLink newCurrentLink = new IdentityLink();
            newCurrentLink.setIdentityGroup(group);
            newCurrentLink.setAppUser(currentUser);
            identityLinkRepository.save(newCurrentLink);
        } else {
            group = currentUserLinkOpt.get().getIdentityGroup();
        }

        Optional<IdentityLink> targetUserLinkOpt = identityLinkRepository.findByAppUserId(targetUser.getId());
        if (targetUserLinkOpt.isPresent()) {
            if (!targetUserLinkOpt.get().getIdentityGroup().getId().equals(group.getId())) {
                throw new RuntimeException("The target account is already linked to another user group.");
            }
            return;
        }

        IdentityLink newTargetLink = new IdentityLink();
        newTargetLink.setIdentityGroup(group);
        newTargetLink.setAppUser(targetUser);
        identityLinkRepository.save(newTargetLink);
    }

    @Transactional(readOnly = true)
    public void validateSwitchAccount(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) return;

        IdentityLink currentLink = identityLinkRepository.findByAppUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("My current account is not linked to any other accounts."));

        IdentityLink targetLink = identityLinkRepository.findByAppUserId(targetUserId)
                .orElseThrow(() -> new RuntimeException("The target account is not linked to any other accounts."));

        if (!currentLink.getIdentityGroup().getId().equals(targetLink.getIdentityGroup().getId())) {
            throw new RuntimeException("You cannot switch to an account that is not in the same group.");
        }
    }

    @Transactional
    public void removeLinkAccount(Long currentUserId, Long targetUserId) {
        // Check that they are in the same group before allowing deletion (or allowing yourself to kick yourself out).
        validateSwitchAccount(currentUserId, targetUserId);
        // Delete the relationship (but not the user)
        identityLinkRepository.deleteByAppUserId(targetUserId);
    }

    @Transactional(readOnly = true)
    public List<AppUserDto> getLinkedAccounts(AppUser currentUser) {
        Optional<IdentityLink> currentLinkOpt = identityLinkRepository.findByAppUserId(currentUser.getId());
        if (currentLinkOpt.isEmpty()) {
            AppUserDto dto = appUserService.convertEntityToDto(currentUser);
            dto.setCurrentUser(true);
            return List.of(dto);
        }

        Long groupId = currentLinkOpt.get().getIdentityGroup().getId();
        List<IdentityLink> allLinksInGroup = identityLinkRepository.findByIdentityGroupId(groupId);

        return allLinksInGroup.stream().map(link -> {
            AppUser user = link.getAppUser();
            AppUserDto dto = appUserService.convertEntityToDto(user);
            dto.setCurrentUser(user.getId().equals(currentUser.getId()));
            return dto;
        }).toList();
    }
}
