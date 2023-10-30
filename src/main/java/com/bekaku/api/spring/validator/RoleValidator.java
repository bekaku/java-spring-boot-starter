package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.model.Role;
import com.bekaku.api.spring.service.RoleService;
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
    private RoleService roleService;
    Logger logger = LoggerFactory.getLogger(RoleValidator.class);

    public void validate(Role role) {
        if (!this.isNew()) {
            Optional<Role> oldData = roleService.findById(role.getId());
            if (oldData.isEmpty()) {
                this.addErrorNotFound();
            }

            if (oldData.isPresent() && !oldData.get().getName().equals(role.getName())) {
                this.validateDuplicate(role);
            }
        } else {
            this.validateDuplicate(role);
        }
        this.checkValidate();
    }

    private void validateDuplicate(Role role) {
        Optional<Role> roleExist = roleService.findByNameAndFrontEnd(role.getName(), role.getFrontEnd());
        if (roleExist.isPresent()) {
            this.addErrorDuplicate(role.getName());
        }
    }
}
