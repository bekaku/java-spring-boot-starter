package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.service.AppRoleService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RoleValidator extends BaseValidator {

    private final AppRoleService appRoleService;

    public RoleValidator(AppRoleService appRoleService, I18n i18n) {
        super(i18n);
        this.appRoleService = appRoleService;
    }

    public void validateCreate(AppRole appRole) {
        List<String> errors = new ArrayList<>();
        this.validateDuplicate(appRole, errors);
        this.checkValidate(errors);
    }

    public void validateUpdate(AppRole appRole) {
        List<String> errors = new ArrayList<>();
        Optional<AppRole> oldData = appRoleService.findById(appRole.getId());
        if (oldData.isEmpty()) {
            this.addErrorNotFound(errors);
        }

        if (oldData.isPresent() && !oldData.get().getName().equals(appRole.getName())) {
            this.validateDuplicate(appRole, errors);
        }
        this.checkValidate(errors);
    }

    private void validateDuplicate(AppRole appRole, List<String> errors) {
        Optional<AppRole> roleExist = appRoleService.findByName(appRole.getName());
        if (roleExist.isPresent()) {
            this.addErrorDuplicate(errors, appRole.getName());
        }
    }
}
