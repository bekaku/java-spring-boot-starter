package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Base class for domain validators. Validators are singleton beans shared by concurrent requests, so this
 * class keeps no per-call state: each validate method creates its own error list, passes it to the
 * {@code addError*} helpers and finishes with {@link #checkValidate(List)}.
 */
public abstract class BaseValidator {

    private final I18n i18n;

    protected BaseValidator(I18n i18n) {
        this.i18n = i18n;
    }

    protected I18n getI18n() {
        return i18n;
    }

    protected void addErrorDuplicate(List<String> errors, String data) {
        errors.add(i18n.getMessage("error.validateDuplicate", data));
    }

    protected void addErrorNotFound(List<String> errors) {
        errors.add(i18n.getMessage("error.dataNotfound"));
    }

    protected void addErrorRequireField(List<String> errors, String data) {
        errors.add(i18n.getMessage("error.validateRequireField", data));
    }

    /**
     * Throws {@code 400 ApiError} with message {@code error.error} and the collected errors, if there are any.
     */
    protected void checkValidate(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"), errors));
        }
    }
}
