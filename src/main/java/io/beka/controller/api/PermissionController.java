package io.beka.controller.api;


import io.beka.configuration.I18n;
import io.beka.exception.InvalidRequestException;
import io.beka.mapper.PermissionMapper;
import io.beka.dto.PermissionDto;
import io.beka.model.Permission;
import io.beka.repository.PermissionRepository;
import io.beka.service.PermissonService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    @Autowired
    private final PermissonService permissonService;

    @Autowired
    private final I18n i18n;

    @Autowired
    private final PermissionMapper permissionMapper;

    @Autowired
    private final PermissionRepository permissionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody PermissionDto dto, BindingResult bindingResult) {

        Permission permission = permissonService.convertDtoToEntity(dto);

        if (permission.getName() == null || permission.getName().length() == 0) {
            bindingResult.rejectValue("name", "Blank", "can't be empty");
        }
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        permissonService.save(permission);
//        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("postData", permission);
//        }});
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity update(@Valid @RequestBody PermissionDto dto, BindingResult bindingResult) {

        Optional<Permission> optional = permissonService.findById(dto.getId());
        if (optional.isPresent()) {
            Permission permission = permissonService.convertDtoToEntity(dto);
            permissonService.save(permission);
//            return ResponseEntity.ok(new HashMap<String, Object>() {{
//                put("updateData", permission);
//            }});
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }


        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity findAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "limit", defaultValue = "20") int limit,
                                  @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

//        List<Permission> permissionList = permissonService.findAll();
//        List<PermissionDto> dataList = permissionList.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
/*
        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("headerLocale", locale);
//            put("currentLocale", LocaleContextHolder.getLocale());
//            put("entityName", i18n.getMessage("app.name"));
//            put("dataList", dataList);
//            put("permissionByMapper", permissonService.findAllViaMapper(new Page(offset, limit)));
//            put("permissionsByMapper", permissionMapper.findAllWithPaging(new Page(offset, limit)));
//            put("hibernateFindAll", permissionRepository.findAll());

//            put("hibernateFindAllSort", permissionRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
//            put("hibernateFindAllPaging", permissionRepository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"))));
//            put("hibernateFindAllPaging", permissonService.findAllWithPaging(0, 1, Sort.by(Sort.Direction.ASC, "id")));
//            put("findByName", permissionRepository.findByName("permission_code"));
//            put("findAllByCrudTable", permissionRepository.findAllByCrudTable("app_user"));
//            put("findAllNativeQuesruByLikeCrudTable", permissionRepository.findAllNativeQuesruByLikeCrudTable("app_user"));
//            put("findByQueryName", permissionRepository.findByQueryName("permission_code"));
//            put("findCustomAllByCrudTableAndActive", permissionRepository.findCustomAllByCrudTableAndActive("app_user", true));
//            put("findAllByCustomObject", permissionRepository.findAllByCustomObject());
        }});
        */

        return new ResponseEntity<>(permissonService.findAllWithPaging(page, limit, Permission.getSort()), HttpStatus.OK);
    }
}
//@Getter
//@JsonRootName("permision")
//@NoArgsConstructor
//class PermssionsParam {
//    @NotBlank(message = "can't be empty")
//    private String name;
//    private String description;
//    private String crudTable;
//}
