package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.service.UserService;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoArgsConstructor
@Component
public class UserValidator extends BaseValidator {

    @Autowired
    private UserService userService;

    @Autowired
    private I18n i18n;
    Logger logger = LoggerFactory.getLogger(UserValidator.class);

    public void validate(User user) {
        if (!this.isNew()) {
            Optional<User> oldData = userService.findById(user.getId());
            if (oldData.isEmpty()) {
                this.addErrorNotFound();
            }
            if (oldData.isPresent() && !oldData.get().getEmail().equals(user.getEmail())) {
                this.validateDuplicate(user);
            }
            if (oldData.isPresent() && user.getUsername() != null && !oldData.get().getUsername().equals(user.getUsername())) {
                this.validateDuplicateUsername(user);
            }
        } else {
            this.validateDuplicate(user);
            this.validateDuplicateUsername(user);
        }
        this.checkValidate();
    }

    private void validateDuplicate(User user) {
        Optional<User> findExist = userService.findByEmail(user.getEmail());
        findExist.ifPresent(value -> this.addError(i18n.getMessage("error.validateDuplicateEmail", value.getEmail())));
    }

    private void validateDuplicateUsername(User user) {
        if(user.getUsername()!=null){
            Optional<User> findUsernameExist = userService.findByUsername(user.getUsername());
            findUsernameExist.ifPresent(value -> this.addError(i18n.getMessage("error.validateDuplicateUsername", value.getUsername())));
        }
    }
}
