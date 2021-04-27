package io.beka.configuration.security.method;

import io.beka.dto.UserDto;
import io.beka.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

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

    /**
     * Custom 'isHasPermission()' expression
     */
    public boolean isHasPermission(String permission) {
        boolean hasPermit = permissionService.isHasPermission(this.userDto.getId(), permission);
        logger.info("isHasPermission >  permission= '{}', userDetail= {}, hasPermit {}", permission, this.userDto, hasPermit);
//        return permissionService.isHasPermission(this.userDto.getId(), permission);
        return true;
    }
}
