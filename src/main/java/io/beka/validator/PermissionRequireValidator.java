package io.beka.validator;

import io.beka.annotation.PermissionRequire;
import io.beka.controller.api.RoleController;
import io.beka.dto.UserDto;
import io.beka.exception.ApiError;
import io.beka.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PermissionRequireValidator implements ConstraintValidator<PermissionRequire, String> {
    private String permissionName;
    private boolean isAdmin;

    Logger logger = LoggerFactory.getLogger(PermissionRequireValidator.class);

    @Override
    public void initialize(PermissionRequire constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        permissionName = constraintAnnotation.permission();
        isAdmin = constraintAnnotation.isAdimin();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDetails = (UserDto) authentication.getPrincipal();
        logger.info("PermissionRequireValidator > isValid >permission :{}, userDetails : {}", this.permissionName, userDetails);
        return false;
    }
}
