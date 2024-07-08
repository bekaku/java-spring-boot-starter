package com.bekaku.api.spring.controller.api;


import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.dto.PermissionDto;
import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.model.Permission;
import com.bekaku.api.spring.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/permission")
@RequiredArgsConstructor
public class PermissionController extends BaseApiController {

    private final PermissionService permissionService;
    private final I18n i18n;
    Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Value("classpath:/acl.json")
    private Resource jsonAcl;

    //http://localhost:8084/api/permission?page=0&size=10&sort=code,asc&_q=code:permission_list,name:user_list,id>10,id>=20,id!=10,id<10,id<=10,id=1
    @PreAuthorize("isHasPermission('permission_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
//        if(pageable.getSort().isEmpty()){
//            Pageable pageable1 = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Permission.getSort());
//        }

        SearchSpecification<Permission> specification = new SearchSpecification<>(getSearchCriteriaList());
//        specification.add(new SearchCriteria("code", "api_client", SearchOperation.MATCH));
        /*
        Page<Permission> result = permissonService.findAllWithSearchSpecification(specification, pageable);
        ResponseListDto<PermissionDto> list = new ResponseListDto<>(result.getContent()
                .stream()
                .map(t-> permissonService.convertEntityToDto(t))
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
         */

//        ResponseListDto<PermissionDto> dto = permissonService.findAllWithSearch(specification, pageable);
//        return this.responseEntity(dto, HttpStatus.OK);

//        return this.responseEntity(permissonService.findAllWithPaging(!pageable.getSort().isEmpty() ? pageable :
//               PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Permission.getSort())), HttpStatus.OK);
        return this.responseEntity(permissionService.findAllWithSearch(specification, getPageable(pageable, Permission.getSort())), HttpStatus.OK);


//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("datas", permissonService.findAllPaging(!pageable.getSort().isEmpty() ? pageable : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Permission.getSort())));
//            put("pageable", pageable);
//        }}, HttpStatus.OK);
    }

    private static Sort getSort() {
        return Sort.by("code").ascending()
                .and(Sort.by("operationType").ascending());
    }

    @PreAuthorize("isHasPermission('role_manage')")
    @GetMapping("/findAllBackendPermission")
    public ResponseEntity<Object> findAllBackendPermission() {
        return this.responseEntity(permissionService.findAllBy(false, getSort()), HttpStatus.OK);
    }

    @GetMapping("/findAllFrontendPermission")
    public ResponseEntity<Object> findAllFrontendPermission() {
        return this.responseEntity(permissionService.findAllBy(true, getSort()), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('permission_manage')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody PermissionDto dto) {

        Permission permission = permissionService.convertDtoToEntity(dto);
        Optional<Permission> permissionExist = permissionService.findByCode(dto.getCode());
        if (permissionExist.isPresent()) {
            throw this.responseErrorDuplicate(dto.getCode());
        }
        permissionService.save(permission);
//        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("postData", permission);
//        }});
//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("postData", permission);
//            put("item2", "item2");
//        }}, HttpStatus.OK);
        return this.responseEntity(permissionService.convertEntityToDto(permission), HttpStatus.CREATED);
    }

    @PreAuthorize("isHasPermission('permission_manage')")
    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody PermissionDto dto) {
        Permission permission = permissionService.convertDtoToEntity(dto);
        Optional<Permission> oldData = permissionService.findById(dto.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (!oldData.get().getCode().equals(permission.getCode())) {
            Optional<Permission> permissionExist = permissionService.findByCode(dto.getCode());
            if (permissionExist.isPresent()) {
                throw this.responseErrorDuplicate(dto.getCode());
            }
        }
        permissionService.update(permission);
        return this.responseEntity(permissionService.convertEntityToDto(permission), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('permission_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<Permission> permission = permissionService.findById(id);
        if (permission.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(permissionService.convertEntityToDto(permission.get()), HttpStatus.OK);

//        return this.responseEntity(permissionService.findAllCustom(), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('permission_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<Permission> permission = permissionService.findById(id);
        if (permission.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        permissionService.delete(permission.get());
        return this.responseDeleteMessage();
    }

    @GetMapping("/userAcl")
    public ResponseEntity<Object> userAcl(@AuthenticationPrincipal UserDto userAuthen,
                                          @RequestParam(value = "getMenuList", required = false, defaultValue = "1") boolean getMenuList) {
        if (userAuthen == null) {
            throw this.responseErrorNotfound();
        }
        List<String> userPermissions = permissionService.findAllPermissionCodeByUserId(userAuthen.getId(), false);
        JSONArray filterMenus = new JSONArray();
        if (getMenuList) {
            JSONArray rawNavMenus = getJsonFromResource(jsonAcl);
            filterMenus = checkAclPermisison(rawNavMenus, userPermissions);
        }

        JSONArray finalFilterMenus = filterMenus;
        return this.responseEntity(new HashMap<String, Object>() {{
            put("menus", finalFilterMenus);
            put("permissions", userPermissions);
        }}, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    private JSONArray checkAclPermisison(JSONArray aclList, List<String> userPermissions) {
        JSONArray aclFinal = new JSONArray();
        JSONObject menu;
        for (Object o : aclList) {
            //Level 1
            menu = new JSONObject();
            JSONObject menuLevel1 = (JSONObject) o;
            if (menuLevel1 != null) {
                if (menuLevel1.containsKey("header")) {
                    menu.put("header", menuLevel1.get("header"));
                }
                if (menuLevel1.containsKey("border")) {
                    menu.put("border", menuLevel1.get("border"));
                }
                //child pages
                if (menuLevel1.containsKey("pages")) {
                    JSONArray pages = (JSONArray) menuLevel1.get("pages");
                    JSONArray filterPages = new JSONArray();
                    if (pages != null && !pages.isEmpty()) {
                        for (Object page : pages) {
                            JSONObject p = (JSONObject) page;
                            if (p != null) {
                                //if have child pages
                                if (p.containsKey("items")) {
                                    JSONArray pageItems = (JSONArray) p.get("items");
                                    if (!pageItems.isEmpty()) {
                                        JSONArray childs = new JSONArray();
                                        for (Object pageItem : pageItems) {
                                            JSONObject item = (JSONObject) pageItem;
                                            if (item != null) {
                                                String permission = item.containsKey("permission") ? (String) item.get("permission") : null;
                                                boolean isPermised = permission == null || userPermissions.contains(permission);
                                                if (isPermised) {
                                                    childs.add(item);
                                                }
                                            }
                                        }
                                        if (!childs.isEmpty()) {
                                            JSONObject menuHaveChild = new JSONObject();
                                            if (p.containsKey("title")) {
                                                menuHaveChild.put("title", p.get("title"));
                                            }
                                            if (p.containsKey("icon")) {
                                                menuHaveChild.put("icon", p.get("icon"));
                                            }
                                            if (p.containsKey("color")) {
                                                menuHaveChild.put("color", p.get("color"));
                                            }
                                            if (p.containsKey("iconText")) {
                                                menuHaveChild.put("iconText", p.get("iconText"));
                                            }
                                            if (p.containsKey("noActiveLink")) {
                                                menuHaveChild.put("noActiveLink", p.get("noActiveLink"));
                                            }
                                            if (p.containsKey("to")) {
                                                menuHaveChild.put("to", p.get("to"));
                                            }
                                            if (p.containsKey("border")) {
                                                menuHaveChild.put("border", p.get("border"));
                                            }
                                            menuHaveChild.put("items", childs);
                                            filterPages.add(menuHaveChild);
                                        }
                                    }
                                } else {
                                    String permission = p.containsKey("permission") ? (String) p.get("permission") : null;
                                    boolean isPermised = permission == null || userPermissions.contains(permission);
                                    if (isPermised) {
                                        filterPages.add(p);
                                    }
                                }
                            }
                        }
                    }
                    menu.put("pages", filterPages);
                }
                if (menu.containsKey("pages")) {
                    JSONArray pages = (JSONArray) menu.get("pages");
                    if (!pages.isEmpty()) {
                        aclFinal.add(menu);
                    }
                }
            }
        }

        return aclFinal;
    }

    private JSONArray getJsonFromResource(Resource resource) {
        JSONArray jsonArray;
        try {
//            File file = resource.getFile();
            InputStream file = resource.getInputStream();
            String content = new String(file.readAllBytes(), StandardCharsets.UTF_8);
//            String content = new String(Files.readAllBytes(file.toPath()));
            JSONParser parser = new JSONParser();
            Object object = parser.parse(content);
            jsonArray = (JSONArray) object;
        } catch (ParseException | IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, i18n.getMessage("error.error"), e.getMessage());
        }

        return jsonArray;
    }

}