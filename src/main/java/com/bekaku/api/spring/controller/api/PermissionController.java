package com.bekaku.api.spring.controller.api;


import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.PermissionDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.Permission;
import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/api/permission")
@RequiredArgsConstructor
public class PermissionController extends BaseApiController {

    private final PermissionService permissionService;
    private final I18n i18n;

    @Value("classpath:/acl.json")
    private Resource jsonAcl;
    // Gson instance for conversions
    private final Gson gson = new Gson();


    @PreAuthorize("@permissionChecker.hasPermission('permission_list')")
    @GetMapping("/findAllLikeByCode")
    public List<PermissionDto> findAllLikeByCode(@RequestParam("_q") String code, Pageable pageable) {
        return permissionService.findAllLikeByCode(code, getPageable(pageable, Permission.getSort()));
    }

    //http://localhost:8084/api/permission?page=0&size=10&sort=code,asc&_q=code:permission_list,name:user_list,id>10,id>=20,id!=10,id<10,id<=10,id=1
    @PreAuthorize("@permissionChecker.hasPermission('permission_list')")
    @GetMapping
    public ResponseListDto<PermissionDto> findAll(Pageable pageable) {
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
        return permissionService.findAllWithSearch(specification, getPageable(pageable, Permission.getSort()));

//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("datas", permissonService.findAllPaging(!pageable.getSort().isEmpty() ? pageable : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Permission.getSort())));
//            put("pageable", pageable);
//        }}, HttpStatus.OK);
    }

    private static Sort getSort() {
        return Sort.by("code").ascending()
                .and(Sort.by("operationType").ascending());
    }

    @PreAuthorize("@permissionChecker.hasPermission('app_role_manage')")
    @GetMapping("/findAllPermission")
    public ResponseEntity<Object> findAllPermission() {
        return this.responseEntity(permissionService.findAllBy(getSort()), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('permission_manage')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody PermissionDto dto) {

        log.info("dto: {}", dto.getDescription());
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

    @PreAuthorize("@permissionChecker.hasPermission('permission_manage')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody PermissionDto dto, @PathVariable("id") long id) {
        Permission permission = permissionService.convertDtoToEntity(dto);
        Optional<Permission> oldData = permissionService.findById(id);
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (!oldData.get().getCode().equals(permission.getCode())) {
            Optional<Permission> permissionExist = permissionService.findByCode(dto.getCode());
            if (permissionExist.isPresent()) {
                throw this.responseErrorDuplicate(dto.getCode());
            }
        }
        permissionService.save(permission);
        return this.responseEntity(permissionService.convertEntityToDto(permission), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('permission_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<Permission> permission = permissionService.findById(id);
        if (permission.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(permissionService.convertEntityToDto(permission.get()), HttpStatus.OK);

//        return this.responseEntity(permissionService.findAllCustom(), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('permission_manage')")
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
    public ResponseEntity<Object> userAcl(@AuthenticationPrincipal AppUserDto userAuthen,
                                          @RequestParam(value = "getMenuList", required = false, defaultValue = "0") boolean getMenuList) {
        if (userAuthen == null) {
            throw this.responseErrorNotfound();
        }
        List<String> userPermissions = permissionService.findAllPermissionCodeByUserId(userAuthen.getId());
        JsonArray filterMenus = new JsonArray();

        if (getMenuList) {
            JsonArray rawNavMenus = getJsonFromResource(jsonAcl);
            filterMenus = checkAclPermisison(rawNavMenus, userPermissions);
        }

        // Convert Gson JsonArray to standard Java List for correct serialization by Spring/Jackson
        List<Object> finalFilterMenusList = gson.fromJson(filterMenus, List.class);

        return this.responseEntity(new HashMap<String, Object>() {{
            put("menus", finalFilterMenusList);
            put("permissions", userPermissions);
        }}, HttpStatus.OK);
    }

    private JsonArray checkAclPermisison(JsonArray aclList, List<String> userPermissions) {
        JsonArray aclFinal = new JsonArray();
        JsonObject menu;

        // Loop through JsonArray elements
        for (JsonElement o : aclList) {
            // Level 1
            menu = new JsonObject();
            JsonObject menuLevel1 = o.getAsJsonObject();

            if (menuLevel1 != null) {
                // Copy properties if they exist
                copyProperty(menuLevel1, menu, "header");
                copyProperty(menuLevel1, menu, "border");

                // Child pages
                if (menuLevel1.has("pages")) {
                    JsonArray pages = menuLevel1.getAsJsonArray("pages");
                    JsonArray filterPages = new JsonArray();

                    if (pages != null && !pages.isEmpty()) {
                        for (JsonElement page : pages) {
                            JsonObject p = page.getAsJsonObject();
                            if (p != null) {
                                // If have child items
                                if (p.has("items")) {
                                    JsonArray pageItems = p.getAsJsonArray("items");
                                    if (!pageItems.isEmpty()) {
                                        JsonArray childs = getJsonArray(userPermissions, pageItems);
                                        if (!childs.isEmpty()) {
                                            JsonObject menuHaveChild = new JsonObject();
                                            copyProperty(p, menuHaveChild, "title");
                                            copyProperty(p, menuHaveChild, "icon");
                                            copyProperty(p, menuHaveChild, "color");
                                            copyProperty(p, menuHaveChild, "iconText");
                                            copyProperty(p, menuHaveChild, "noActiveLink");
                                            copyProperty(p, menuHaveChild, "to");
                                            copyProperty(p, menuHaveChild, "border");

                                            menuHaveChild.add("items", childs);
                                            filterPages.add(menuHaveChild);
                                        }
                                    }
                                } else {
                                    // Check permission for single item
                                    String permission = (p.has("permission") && !p.get("permission").isJsonNull())
                                            ? p.get("permission").getAsString()
                                            : null;

                                    boolean isPermised = permission == null || userPermissions.contains(permission);
                                    if (isPermised) {
                                        filterPages.add(p);
                                    }
                                }
                            }
                        }
                    }
                    menu.add("pages", filterPages);
                }

                // Only add menu if it has pages
                if (menu.has("pages")) {
                    JsonArray pages = menu.getAsJsonArray("pages");
                    if (!pages.isEmpty()) {
                        aclFinal.add(menu);
                    }
                }
            }
        }

        return aclFinal;
    }

    private static @NotNull JsonArray getJsonArray(List<String> userPermissions, JsonArray pageItems) {
        JsonArray childs = new JsonArray();
        for (JsonElement pageItem : pageItems) {
            JsonObject item = pageItem.getAsJsonObject();
            if (item != null) {
                String permission = (item.has("permission") && !item.get("permission").isJsonNull())
                        ? item.get("permission").getAsString()
                        : null;

                boolean isPermised = permission == null || userPermissions.contains(permission);
                if (isPermised) {
                    childs.add(item);
                }
            }
        }
        return childs;
    }

    // Helper to reduce repetitive code
    private void copyProperty(JsonObject source, JsonObject target, String key) {
        if (source.has(key)) {
            target.add(key, source.get(key));
        }
    }

    private JsonArray getJsonFromResource(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            // Gson parser - much cleaner than reading bytes manually
            JsonElement element = JsonParser.parseReader(reader);
            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            }
            return new JsonArray(); // Fallback if root is not an array
        } catch (JsonSyntaxException | IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, i18n.getMessage("error.error"), e.getMessage());
        }
    }

}