package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.service.AppUserService;
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
    private AppUserService appUserService;

    @Autowired
    private I18n i18n;
    Logger logger = LoggerFactory.getLogger(UserValidator.class);

    public void validate(AppUser appUser) {
        if (!this.isNew()) {
            Optional<AppUser> oldData = appUserService.findById(appUser.getId());
            if (oldData.isEmpty()) {
                this.addErrorNotFound();
            }
            if (oldData.isPresent() && !oldData.get().getEmail().equals(appUser.getEmail())) {
                this.validateDuplicate(appUser);
            }
            if (oldData.isPresent() && appUser.getUsername() != null && !oldData.get().getUsername().equals(appUser.getUsername())) {
                this.validateDuplicateUsername(appUser);
            }
        } else {
            this.validateDuplicate(appUser);
            this.validateDuplicateUsername(appUser);
        }
        this.checkValidate();
    }

    private void validateDuplicate(AppUser appUser) {
        Optional<AppUser> findExist = appUserService.findByEmail(appUser.getEmail());
        findExist.ifPresent(value -> this.addError(i18n.getMessage("error.validateDuplicateEmail", value.getEmail())));
    }

    private void validateDuplicateUsername(AppUser appUser) {
        if(appUser.getUsername()!=null){
            Optional<AppUser> findUsernameExist = appUserService.findByUsername(appUser.getUsername());
            findUsernameExist.ifPresent(value -> this.addError(i18n.getMessage("error.validateDuplicateUsername", value.getUsername())));
        }
    }
}
