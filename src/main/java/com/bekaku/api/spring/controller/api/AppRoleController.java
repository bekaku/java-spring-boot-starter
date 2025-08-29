package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AppRoleDto;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.model.Permission;
import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.service.AppRoleService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.validator.RoleValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping(path = "/api/appRole")
@RestController
@RequiredArgsConstructor
public class AppRoleController extends BaseApiController {

    private final AppRoleService appRoleService;
    private final PermissionService permissionService;
    private final AppUserService appUserService;
    private final I18n i18n;
    private final RoleValidator roleValidator;


    @Value("${app.loging.enable}")
    boolean logEnable;

    @Value("${app.test-prop}")
    String testProperties;

    @Value("${environments.production}")
    boolean isProduction;

    @PreAuthorize("isHasPermission('app_role_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        SearchSpecification<AppRole> specification = new SearchSpecification<>(getSearchCriteriaList());
        return this.responseEntity(appRoleService.findAllWithSearch(specification, getPageable(pageable, AppRole.getSort())), HttpStatus.OK);
    }

    @GetMapping("/findAll")
    public ResponseEntity<Object> findAll() {
        return this.responseEntity(appRoleService.findAllByOrderByNameAsc()
                        .stream()
                        .map(appRoleService::convertEntityToDto)
                        .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('app_role_manage')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AppRoleDto dto) {
        return this.responseEntity(createProcess(dto), HttpStatus.CREATED);
    }

    private AppRoleDto createProcess(AppRoleDto dto) {

        AppRole appRole = appRoleService.convertDtoToEntity(dto);
        roleValidator.validate(appRole);
        setRolePermission(dto, appRole);
        appRoleService.save(appRole);
        return appRoleService.convertEntityToDto(appRole);
    }

    private void setRolePermission(AppRoleDto dto, AppRole appRole) {
        if (dto.getSelectdPermissions().length > 0) {
            Optional<Permission> permission;
            for (long permissionId : dto.getSelectdPermissions()) {
                permission = permissionService.findById(permissionId);
                permission.ifPresent(value -> appRole.getPermissions().add(value));
            }
        }
    }

    @PreAuthorize("isHasPermission('app_role_manage')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody AppRoleDto dto, @PathVariable("id") long id) {
        Optional<AppRole> role = appRoleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(updateProcess(role.get(), dto), HttpStatus.OK);
    }

    private AppRoleDto updateProcess(AppRole appRole, AppRoleDto dto) {
        appRole.update(dto.getName(), dto.isActive());
        roleValidator.validate(appRole);
        // delete old permissin for this group
        appRole.setPermissions(new HashSet<>());
        appRoleService.update(appRole);

        // add permission to this role
        if (dto.getSelectdPermissions().length > 0) {
            setRolePermission(dto, appRole);
        } else {
            appRole.setPermissions(new HashSet<>());
        }
        appRoleService.update(appRole);
        return appRoleService.convertEntityToDto(appRole);
    }

    @PreAuthorize("isHasPermission('app_role_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<AppRole> role = appRoleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        AppRoleDto appRoleDto = appRoleService.convertEntityToDto(role.get());
        List<Long> permissonSelectedList = permissionService.findAllPermissionIdByRoleId(id);
        if (!permissonSelectedList.isEmpty()) {
            appRoleDto.setSelectdPermissions(permissonSelectedList.toArray(new Long[0]));
        }
        return this.responseEntity(appRoleDto, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('app_role_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<AppRole> role = appRoleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        appRoleService.delete(role.get());
        return this.responseDeleteMessage();
    }
}
