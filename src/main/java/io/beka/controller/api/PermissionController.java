package io.beka.controller.api;


import io.beka.configuration.I18n;
import io.beka.dto.PermissionDto;
import io.beka.model.Permission;
import io.beka.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/permission")
@RequiredArgsConstructor
public class PermissionController extends BaseApiController {

    private final PermissionService permissonService;
    private final I18n i18n;
    Logger logger = LoggerFactory.getLogger(PermissionController.class);

    //http://localhost:8084/api/permission?page=0&size=10&sort=code,asc
    @PreAuthorize("isHasPermission('permission_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
//        if(pageable.getSort().isEmpty()){
//            Pageable pageable1 = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Permission.getSort());
//        }
        return this.responseEntity(permissonService.findAllWithPaging(!pageable.getSort().isEmpty() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Permission.getSort())), HttpStatus.OK);
//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("datas", permissonService.findAllPaging(!pageable.getSort().isEmpty() ? pageable : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Permission.getSort())));
//            put("pageable", pageable);
//        }}, HttpStatus.OK);
    }
//    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
//                                          @RequestParam(value = "limit", defaultValue = "20") int limit) {
//
//        return this.responseEntity(permissonService.findAllWithPaging(new Paging(page, limit), Permission.getSort()), HttpStatus.OK);
//    }

    @PreAuthorize("isHasPermission('permission_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody PermissionDto dto) {

        Permission permission = permissonService.convertDtoToEntity(dto);
        Optional<Permission> permissionExist = permissonService.findByCode(dto.getCode());
        if (permissionExist.isPresent()) {
            throw this.responseErrorDuplicate(dto.getCode());
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

    @PreAuthorize("isHasPermission('permission_edit')")
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
                throw this.responseErrorDuplicate(dto.getCode());
            }
        }
        permissonService.update(permission);
        return this.responseEntity(permissonService.convertEntityToDto(permission), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('permission_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<Permission> permission = permissonService.findById(id);
        if (permission.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(permissonService.convertEntityToDto(permission.get()), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('permission_delete')")
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