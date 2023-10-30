package com.bekaku.api.spring.annotation;

import com.bekaku.api.spring.validator.PermissionRequireValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PermissionRequireValidator.class})
public @interface PermissionRequire {
    String message() default "Access to this link is not allowed.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String permission() default "";

    boolean isAdimin() default false;
}
