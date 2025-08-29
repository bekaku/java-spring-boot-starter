package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.ImageDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.exception.BaseResponseException;
import com.bekaku.api.spring.mapper.UserMapper;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.mybatis.AppUserMybatis;
import com.bekaku.api.spring.repository.AppUserRepository;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.vo.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.bekaku.api.spring.util.ConstantData.ASYNC_TASK_NAME;

@RequiredArgsConstructor
@Transactional
@Service
public class AppUserServiceImpl extends BaseResponseException implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMybatis appUserMybatis;
    private final FileManagerService fileManagerService;
    private final UserMapper modelMapper;
    private final I18n i18n;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AppUserDto> findAllWithPaging(Pageable pageable) {
        Page<AppUser> result = appUserRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AppUserDto> findAllWithSearch(SearchSpecification<AppUser> specification, Pageable pageable) {
        Page<AppUser> result = appUserRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AppUserDto> findAllBy(Specification<AppUser> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<AppUser> findAllPageSpecificationBy(Specification<AppUser> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<AppUser> findAllPageSearchSpecificationBy(SearchSpecification<AppUser> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<AppUserDto> getListFromResult(Page<AppUser> result) {
        return new ResponseListDto<>(result.getContent().stream().map(this::convertEntityToDto).collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    @Override
    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser update(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppUser> findById(Long id) {
        return appUserRepository.findById(id);
    }

    @Override
    public void delete(AppUser appUser) {
        appUserRepository.delete(appUser);
    }

    @Override
    public void deleteById(Long id) {
        appUserRepository.deleteById(id);
    }

    @Override
    public AppUserDto convertEntityToDto(AppUser appUser) {
        AppUserDto dto = modelMapper.toDto(appUser);
        if (appUser.getAvatarFile() != null) {
            Optional<ImageDto> imageDto = fileManagerService.findImageDtoBy(appUser.getAvatarFile());
            imageDto.ifPresent(dto::setAvatar);
        } else {
            dto.setAvatar(fileManagerService.getDefaultAvatar());
        }
        if (appUser.getCoverFile() != null) {
            Optional<ImageDto> imageDto = fileManagerService.findImageDtoBy(appUser.getCoverFile());
            imageDto.ifPresent(dto::setCover);
        }
        if (!appUser.getAppRoles().isEmpty()) {
            List<Long> rolesId = new ArrayList<>();
            for (AppRole r : appUser.getAppRoles()) {
                rolesId.add(r.getId());
            }
            dto.setSelectedRoles(rolesId);
        }
        return dto;
    }


    @Override
    public AppUser convertDtoToEntity(AppUserDto userData) {
        return modelMapper.toEntity(userData);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppUser> findByUUID(String salt) {
        return appUserRepository.findBySalt(salt);
    }

    @Override
    public void updatePasswordBy(AppUser appUser, String password) {
        appUserRepository.updatePasswordBy(appUser, password);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppUser> findActiveByEmailOrUserName(String email) {
        Optional<AppUser> user = appUserRepository.findByEmailOrUsername(email);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        if (!user.get().isActive()) {
            return Optional.empty();
        }
        return user;
    }

    //mapper
    @Transactional(readOnly = true)
    @Override
    public List<AppUserDto> findAllUserData(Paging page) {
        return appUserMybatis.findAll(page);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppUserDto> findUserDataById(Long id) {
        return appUserMybatis.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppUserDto> findUserDataByUsername(String username) {
        return appUserMybatis.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppUserDto> findUserDataByEmail(String email) {
        return appUserMybatis.findByEmail(email);
    }

    @Override
    public void requireTheSameUser(Long leftUserId, Long rightUserId) {
        if (!Objects.equals(leftUserId, rightUserId)) {
            throw this.responseErrorForbidden();
        }
    }

    @Override
    public void requireTheSameUser(AppUser leftAppUser, AppUser rightAppUser) {
        if (!Objects.equals(leftAppUser.getId(), rightAppUser.getId())) {
            throw this.responseErrorForbidden();
        }
    }

    @Async(ASYNC_TASK_NAME)
    @Override
    public CompletableFuture<String> processAsyncTask() {
        System.out.println("Running in thread: " + Thread.currentThread().getName());
        try {
            Thread.sleep(2000); // simulate delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture("Processed Async Task");
    }

    @Override
    public Page<AppUser> findAllPageBy(Pageable pageable) {
        return appUserRepository.findAll(pageable);
    }

    @Override
    public AppUser findAndValidateAppUserBy(AppUserDto userAuthen) {
        Optional<AppUser> appUser = findById(userAuthen.getId());
        if (appUser.isEmpty()) {
            throw this.responseErrorNotfound(i18n.getMessage("model.appUser"));
        }
        return appUser.get();
    }
}
