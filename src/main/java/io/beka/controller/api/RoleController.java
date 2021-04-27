package io.beka.controller.api;

import io.beka.configuration.I18n;
import io.beka.dto.RoleDto;
import io.beka.model.Permission;
import io.beka.model.Role;
import io.beka.service.PermissionService;
import io.beka.service.RoleService;
import io.beka.vo.Paging;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "limit", defaultValue = "20") int limit) {
        logger.info("logEnable {}", logEnable);

        return this.responseEntity(roleService.findAllWithPaging(new Paging(page, limit), Role.getSort()), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('role_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RoleDto dto) {

//        new RoleValidator();
//        throw this.responseError(HttpStatus.BAD_REQUEST, "Validator test", "Just test validator");

        Role role = roleService.convertDtoToEntity(dto);
        Optional<Role> roleExist = roleService.findByName(dto.getName());
        if (roleExist.isPresent()) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.validateDuplicate", dto.getName()));
        }
        // add permission to this role
        setRolePermission(dto, role);

        roleService.save(role);
        return this.responseEntity(roleService.convertEntityToDto(role), HttpStatus.CREATED);
    }

    private void setRolePermission(RoleDto dto, Role role) {
        Set<Permission> permissins = new HashSet<>();
        if (dto.getPermissions().length > 0) {
            Optional<Permission> permission;
            for (int permissionId : dto.getPermissions()) {
                permission = permissionService.findById((long) permissionId);
                permission.ifPresent(permissins::add);
            }
        }
        role.setPermissions(permissins);
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody RoleDto dto) {
        Role role = roleService.convertDtoToEntity(dto);

        Optional<Role> oldData = roleService.findById(role.getId());
        if (oldData.isEmpty()) {
            throw this.responseError(HttpStatus.NOT_FOUND, null, i18n.getMessage("error.dataNotfound"));
        }
        if (!oldData.get().getName().equals(role.getName())) {
            Optional<Role> roleExist = roleService.findByName(role.getName());
            if (roleExist.isPresent()) {
                throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.validateDuplicate", dto.getName()));
            }
        }
        // add permission to this role
        if (dto.getPermissions().length > 0) {
            setRolePermission(dto, role);
        }
        roleService.update(role);
        return this.responseEntity(roleService.convertEntityToDto(role), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseError(HttpStatus.NOT_FOUND, null, i18n.getMessage("error.dataNotfound"));
        }
        return this.responseEntity(roleService.convertEntityToDto(role.get()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw this.responseError(HttpStatus.NOT_FOUND, null, i18n.getMessage("error.dataNotfound"));
        }
        roleService.deleteById(id);
        return this.responseEntity(HttpStatus.OK);
    }
}
