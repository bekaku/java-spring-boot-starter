package io.beka.controller.api;

import io.beka.configuration.I18n;
import io.beka.dto.RoleDto;
import io.beka.model.Permission;
import io.beka.model.Role;
import io.beka.service.PermissionService;
import io.beka.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;

@RequestMapping(path = "/api/role")
@RestController
@RequiredArgsConstructor
public class RoleController extends BaseApiController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final I18n i18n;

    Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Value("${log.enable}")
    boolean logEnable;

    @PreAuthorize("isHasPermission('role_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        logger.info("logEnable {}", logEnable);

        return this.responseEntity(roleService.findAllWithPaging(!pageable.getSort().isEmpty() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Role.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('role_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RoleDto dto) {
        Role role = roleService.convertDtoToEntity(dto);
        Optional<Role> roleExist = roleService.findByName(dto.getName());
        if (roleExist.isPresent()) {
            throw this.responseErrorDuplicate(dto.getName());
        }
        // add permission to this role
        setRolePermission(dto, role);
//        return this.responseEntity(role, HttpStatus.CREATED);
        roleService.save(role);
        return this.responseEntity(roleService.convertEntityToDto(role), HttpStatus.CREATED);
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

    @PreAuthorize("isHasPermission('role_edit')")
    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody RoleDto dto) {
        Role role = roleService.convertDtoToEntity(dto);

        Optional<Role> oldData = roleService.findById(role.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (!oldData.get().getName().equals(role.getName())) {
            Optional<Role> roleExist = roleService.findByName(role.getName());
            if (roleExist.isPresent()) {
                throw this.responseErrorDuplicate(dto.getName());
            }
        }
        // add permission to this role
        if (dto.getSelectdPermissions().length > 0) {
            setRolePermission(dto, role);
        } else {
            role.setPermissions(new HashSet<>());
        }

        roleService.update(role);
        return this.responseEntity(roleService.convertEntityToDto(role), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('role_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(roleService.convertEntityToDto(role.get()), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('role_delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        roleService.deleteById(id);
        return this.responseEntity(HttpStatus.OK);
    }
}
