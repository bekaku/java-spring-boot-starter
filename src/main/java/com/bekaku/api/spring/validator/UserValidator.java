package com.bekaku.api.spring.validator;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.service.AppUserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserValidator extends BaseValidator {

    private final AppUserService appUserService;

    public UserValidator(AppUserService appUserService, I18n i18n) {
        super(i18n);
        this.appUserService = appUserService;
    }

    public void validateCreate(AppUser appUser) {
        List<String> errors = new ArrayList<>();
        this.validateDuplicate(appUser, errors);
        this.validateDuplicateUsername(appUser, errors);
        this.checkValidate(errors);
    }

    public void validateUpdate(AppUser appUser) {
        List<String> errors = new ArrayList<>();
        Optional<AppUser> oldData = appUserService.findById(appUser.getId());
        if (oldData.isEmpty()) {
            this.addErrorNotFound(errors);
        }
        if (oldData.isPresent() && !oldData.get().getEmail().equals(appUser.getEmail())) {
            this.validateDuplicate(appUser, errors);
        }
        if (oldData.isPresent() && appUser.getUsername() != null && !oldData.get().getUsername().equals(appUser.getUsername())) {
            this.validateDuplicateUsername(appUser, errors);
        }
        this.checkValidate(errors);
    }

    private void validateDuplicate(AppUser appUser, List<String> errors) {
        Optional<AppUser> findExist = appUserService.findByEmail(appUser.getEmail());
        findExist.ifPresent(value -> errors.add(getI18n().getMessage("error.validateDuplicateEmail", value.getEmail())));
    }

    private void validateDuplicateUsername(AppUser appUser, List<String> errors) {
        if (appUser.getUsername() != null) {
            Optional<AppUser> findUsernameExist = appUserService.findByUsername(appUser.getUsername());
            findUsernameExist.ifPresent(value -> errors.add(getI18n().getMessage("error.validateDuplicateUsername", value.getUsername())));
        }
    }
}
