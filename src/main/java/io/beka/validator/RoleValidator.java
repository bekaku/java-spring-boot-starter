package io.beka.validator;

import io.beka.model.Role;
import io.beka.service.RoleService;
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
        logger.info("RoleValidator > validate : isNew {} , name of role {}", this.isNew(), role.getName());

        if (!this.isNew()) {
            Optional<Role> oldData = roleService.findById(role.getId());
            if (oldData.isEmpty()) {
                this.addErrorNotFound();
            }

            if (oldData.isPresent() && !oldData.get().getName().equals(role.getName())) {
                this.validateDuplicate(role.getName());
            }
        }else{
            this.validateDuplicate(role.getName());
        }
        this.checkValidate();
    }
    private void validateDuplicate(String name){
        Optional<Role> roleExist = roleService.findByName(name);
        if (roleExist.isPresent()) {
            this.addErrorDuplicate(name);
        }
    }
}
