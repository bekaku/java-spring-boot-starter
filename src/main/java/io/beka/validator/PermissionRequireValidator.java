package io.beka.validator;

import io.beka.annotation.PermissionRequire;
import io.beka.dto.UserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


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
