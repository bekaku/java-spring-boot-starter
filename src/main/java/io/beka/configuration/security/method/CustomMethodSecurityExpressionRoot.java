package io.beka.configuration.security.method;

import io.beka.dto.UserDto;
import io.beka.model.User;
import io.beka.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;
    private final UserDto userDto;
    Logger logger = LoggerFactory.getLogger(CustomMethodSecurityExpressionRoot.class);

    private final UserService userService;

    /**
     * Creates a new instance
     *
     * @param authentication the {@link Authentication} to use. Cannot be null.
     */
    public CustomMethodSecurityExpressionRoot(Authentication authentication, UserService userService) {
        super(authentication);
        this.userDto = (UserDto) authentication.getPrincipal();
        this.userService = userService;
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
        // TODO: Implement
        logger.info("CustomMethodSecurityExpressionRoot > isHasPermission >  permission {}, userDetail : {}", permission, this.userDto);
        Optional<User> user = userService.findById(this.userDto.getId());
        user.ifPresent(value -> logger.info("isHasPermission : username {}", value.getUsername()));
        return true;
    }
}
