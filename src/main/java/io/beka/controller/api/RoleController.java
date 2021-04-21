package io.beka.controller.api;

import io.beka.configuration.I18n;
import io.beka.dto.Paging;
import io.beka.dto.RoleDto;
import io.beka.exception.ApiError;
import io.beka.exception.ApiException;
import io.beka.exception.InvalidRequestException;
import io.beka.model.Permission;
import io.beka.model.Role;
import io.beka.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;

@RequestMapping(path = "/api/role")
@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final I18n i18n;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "limit", defaultValue = "20") int limit) {

        return new ResponseEntity<>(roleService.findAllWithPaging(new Paging(page, limit), Role.getSort()), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RoleDto dto) {

        Role role = roleService.convertDtoToEntity(dto);
        Optional<Role> roleExist = roleService.findByName(dto.getName());
        if (roleExist.isPresent()) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"), i18n.getMessage("error.validateDuplicate", dto.getName())));
        }
        roleService.save(role);
        return new ResponseEntity<>(roleService.convertEntityToDto(role), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody RoleDto dto) {
        Role role = roleService.convertDtoToEntity(dto);

        Optional<Role> oldData = roleService.findById(role.getId());
        if (oldData.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, i18n.getMessage("error.error"), i18n.getMessage("error.dataNotfound")));
        }
        if (!oldData.get().getName().equals(role.getName())) {
            Optional<Role> roleExist = roleService.findByName(role.getName());
            if (roleExist.isPresent()) {
                throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"), i18n.getMessage("error.validateDuplicate", dto.getName())));
            }
        }
        roleService.update(role);
        return new ResponseEntity<>(roleService.convertEntityToDto(role), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id){
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, i18n.getMessage("error.error"), i18n.getMessage("error.dataNotfound")));
        }
        return new ResponseEntity<>(roleService.convertEntityToDto(role.get()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<Role> role = roleService.findById(id);
        if (role.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, i18n.getMessage("error.error"), i18n.getMessage("error.dataNotfound")));
        }
        roleService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
