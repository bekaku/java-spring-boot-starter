package io.beka.controller.api;


import io.beka.configuration.I18n;
import io.beka.dto.PermissionDto;
import io.beka.exception.InvalidRequestException;
import io.beka.mapper.PermissionMapper;
import io.beka.model.Permission;
import io.beka.repository.PermissionRepository;
import io.beka.service.PermissionService;
import io.beka.serviceImpl.PermissionServiceImpl;
import io.beka.vo.Paging;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/permission")
@RequiredArgsConstructor
public class PermissionController extends BaseApiController {

    private final PermissionService permissonService;
    private final I18n i18n;
    Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "limit", defaultValue = "20") int limit) {

        return this.responseEntity(permissonService.findAllWithPaging(new Paging(page, limit), Permission.getSort()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody PermissionDto dto) {

        Permission permission = permissonService.convertDtoToEntity(dto);
        Optional<Permission> permissionExist = permissonService.findByCode(dto.getCode());
        if (permissionExist.isPresent()) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.validateDuplicate", dto.getCode()));
        }
        permissonService.save(permission);
//        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("postData", permission);
//        }});
//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("postData", permission);
//            put("item2", "item2");
//        }}, HttpStatus.OK);
        return this.responseEntity(permissonService.convertEntityToDto(permission), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody PermissionDto dto) {
        Permission permission = permissonService.convertDtoToEntity(dto);
        Optional<Permission> oldData = permissonService.findById(dto.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (!oldData.get().getCode().equals(permission.getCode())) {
            Optional<Permission> permissionExist = permissonService.findByCode(dto.getCode());
            if (permissionExist.isPresent()) {
                throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage("error.validateDuplicate", dto.getCode()));
            }
        }
        permissonService.update(permission);
        return this.responseEntity(permissonService.convertEntityToDto(permission), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<Permission> permission = permissonService.findById(id);
        if (permission.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(permissonService.convertEntityToDto(permission.get()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<Permission> role = permissonService.findById(id);
        if (role.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        permissonService.deleteById(id);
        return this.responseEntity(HttpStatus.OK);
    }

}