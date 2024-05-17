package com.bekaku.api.spring.configuration.security.method;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.util.ConstantData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Stream;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;
    private final UserDto userDto;
    Logger logger = LoggerFactory.getLogger(CustomMethodSecurityExpressionRoot.class);

    private final PermissionService permissionService;

    /**
     * Creates a new instance
     *
     * @param authentication the {@link Authentication} to use. Cannot be null.
     */
    public CustomMethodSecurityExpressionRoot(Authentication authentication, PermissionService permissionService) {
        super(authentication);
        this.userDto = (UserDto) authentication.getPrincipal();
        this.permissionService = permissionService;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    void setThis(Object target) {
        this.target = target;
    }

    @Override
    public Object getThis() {
        return target;
    }

    /*
     * @PreAuthorize("isHasPermission('user_manage||role_manage')")
     * @param permission
     * @return
     */
    public boolean isHasPermission(String permissionSeparated) {
        Long userId = this.userDto.getId();
        boolean isPermited = false;
        // Check if the delimiter exists
        if (permissionSeparated.contains(ConstantData.OR_SEPARATED)) {
            List<String> items = Stream.of(permissionSeparated.split("\\|\\|"))
                    .map(String::trim)
                    .toList();
            if (items.isEmpty()) {
                return false;
            }
            for (String permission : items) {
                isPermited = validatePermit(userId, permission);
                if (isPermited) {
                    break;
                }
            }
        } else {
            isPermited = validatePermit(userId, permissionSeparated);
        }

        return isPermited;
//        return permissionService.isHasPermission(this.userDto.getId(), permission);
    }

    private boolean validatePermit(Long userId, String permission) {
        return permissionService.isHasPermission(userId, permission);
    }
}
