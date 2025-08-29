package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.service.AppRoleService;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoArgsConstructor
@Component
public class RoleValidator extends BaseValidator {

    @Autowired
    private AppRoleService appRoleService;
    Logger logger = LoggerFactory.getLogger(RoleValidator.class);

    public void validate(AppRole appRole) {
        if (!this.isNew()) {
            Optional<AppRole> oldData = appRoleService.findById(appRole.getId());
            if (oldData.isEmpty()) {
                this.addErrorNotFound();
            }

            if (oldData.isPresent() && !oldData.get().getName().equals(appRole.getName())) {
                this.validateDuplicate(appRole);
            }
        } else {
            this.validateDuplicate(appRole);
        }
        this.checkValidate();
    }

    private void validateDuplicate(AppRole appRole) {
        Optional<AppRole> roleExist = appRoleService.findByName(appRole.getName());
        if (roleExist.isPresent()) {
            this.addErrorDuplicate(appRole.getName());
        }
    }
}
