package io.beka.controller.api;


import io.beka.exception.InvalidRequestException;
import io.beka.model.Page;
import io.beka.model.entity.Permissions;
import io.beka.service.PermissonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/api/permissions")
@RequiredArgsConstructor
public class PermissionsController {
    private final PermissonService permissonService;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Permissions permissions, BindingResult bindingResult) {

        if (permissions.getName()==null || permissions.getName().length()==0) {
            bindingResult.rejectValue("name", "Blank", "can't be empty");
        }
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }

        permissonService.save(permissions);

        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("postData", permissions);
        }});
    }
    @GetMapping
    public ResponseEntity getPermissions(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                  @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("permissionsByMapper", permissonService.findAllViaMapper(new Page(offset, limit)));


//            put("permissionsByMapper", permissionsMapper.allPaging(new Page(offset, limit)));
//            put("hibernateFindAll", permissionRepository.findAll());

//            put("hibernateFindAllSort", permissionRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
//            put("hibernateFindAllPaging", permissionRepository.findAll(PageRequest.of(1, 1, Sort.by(Sort.Direction.ASC, "id"))));
//            put("findByName", permissionRepository.findByName("permission_code"));
//            put("findAllByCrudTable", permissionRepository.findAllByCrudTable("app_user"));
//            put("findAllNativeQuesruByLikeCrudTable", permissionRepository.findAllNativeQuesruByLikeCrudTable("app_user"));
//            put("findByQueryName", permissionRepository.findByQueryName("permission_code"));
//            put("findCustomAllByCrudTableAndActive", permissionRepository.findCustomAllByCrudTableAndActive("app_user", true));
//            put("findAllPaging", permissionRepository.findAllPaging(new Page(offset, limit)));
//            put("findAllByCustomObject", permissionRepository.findAllByCustomObject());
        }});
    }
}
//@Getter
//@JsonRootName("permisions")
//@NoArgsConstructor
//class PermssionsParam {
//    @NotBlank(message = "can't be empty")
//    private String name;
//    private String description;
//    private String crudTable;
//}
