package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.RoleDto;
import com.bekaku.api.spring.model.Role;
import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.service.RoleService;
import com.bekaku.api.spring.service.UserService;
import com.bekaku.api.spring.specification.SearchCriteria;
import com.bekaku.api.spring.specification.SearchOperation;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.validator.RoleValidator;
import com.bekaku.api.spring.model.Permission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RequestMapping(path = "/api/role")
@RestController
@RequiredArgsConstructor
public class RoleController extends BaseApiController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final I18n i18n;
    private final RoleValidator roleValidator;

    Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Value("${app.loging.enable}")
    boolean logEnable;

    @Value("${app.test-prop}")
    String testProperties;

    @Value("${environments.production}")
    boolean isProduction;

    @PreAuthorize("isHasPermission('role_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        SearchSpecification<Role> specification = new SearchSpecification<>(getSearchCriteriaList());
        return this.responseEntity(roleService.findAllWithSearch(specification, getPageable(pageable, Role.getSort())), HttpStatus.OK);
    }

    @GetMapping("/findAllBackend")
    public ResponseEntity<Object> findAllBackend() {

        return this.responseEntity(roleService.findAllByFrontEndOrderByNameAsc(false)
                        .stream()
                        .map(roleService::convertEntityToDto)
                        .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('role_manage')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RoleDto dto) {
        return this.responseEntity(createProcess(dto), HttpStatus.CREATED);
    }

    private RoleDto createProcess(RoleDto dto) {

        Role role = roleService.convertDtoToEntity(dto);
        roleValidator.validate(role);
        setRolePermission(dto, role);
        roleService.save(role);
        return roleService.convertEntityToDto(role);
    }

    private void setRolePermission(RoleDto dto, Role role) {
        if (dto.getSelectdPermissions().length > 0) {
            Optional<Permission> permission;
            for (long permissionId : dto.getSelectdPermissions()) {
                permission = permissionService.findById(permissionId);
                permission.ifPresent(value -> role.getPermissions().add(value));
            }
        }
    }

    @PreAuthorize("isHasPermission('role_manage')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody RoleDto dto, @PathVariable("id") long id) {
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(updateProcess(role.get(), dto), HttpStatus.OK);
    }

    private RoleDto updateProcess(Role role, RoleDto dto) {
        role.update(dto.getName(), dto.getNameEn(), dto.isActive(), dto.isFrontEnd());
        roleValidator.validate(role);
        // delete old permissin for this group
        role.setPermissions(new HashSet<>());
        roleService.update(role);

        // add permission to this role
        if (dto.getSelectdPermissions().length > 0) {
            setRolePermission(dto, role);
        } else {
            role.setPermissions(new HashSet<>());
        }
        roleService.update(role);
        return roleService.convertEntityToDto(role);
    }

    @PreAuthorize("isHasPermission('role_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        RoleDto roleDto = roleService.convertEntityToDto(role.get());
        List<Long> permissonSelectedList = permissionService.findAllPermissionIdByRoleId(id);
        if (!permissonSelectedList.isEmpty()) {
            roleDto.setSelectdPermissions(permissonSelectedList.toArray(new Long[0]));
        }
        return this.responseEntity(roleDto, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('role_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        roleService.delete(role.get());
        return this.responseDeleteMessage();
    }
}
