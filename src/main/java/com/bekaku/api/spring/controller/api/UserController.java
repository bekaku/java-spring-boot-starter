package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.*;
import com.bekaku.api.spring.enumtype.AppLocale;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.Role;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.validator.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequestMapping(path = "/api/user")
@RestController
public class UserController extends BaseApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private EncryptService encryptService;
    @Autowired
    private FileManagerService fileManagerService;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private I18n i18n;

    @Value("${app.defaults.userpwd}")
    String defaultUserPwd;

    private final String SHEET_NAME = "users";
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/currentUserData")
    public UserDto currentUserData(@AuthenticationPrincipal UserDto userAuthen) {
        if (userAuthen == null) {
            throw this.responseErrorUnauthorized();
        }
        Optional<User> user = userService.findById(userAuthen.getId());
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        UserDto dto = userService.convertEntityToDto(user.get());
        if (userAuthen.getAccessTokenId() != null) {
            Optional<AccessToken> accessToken = accessTokenService.findById(userAuthen.getAccessTokenId());
            accessToken.ifPresent(token -> dto.setFcmToken(token.getFcmToken()));
        }

        return dto;
    }

    @PutMapping("/updateDefaultLocale")
    public ResponseEntity<Object> updateDefaultLocale(@AuthenticationPrincipal UserDto userAuthen, @RequestParam("locale") AppLocale locale) {
        if (userAuthen != null) {
            Optional<User> user = userService.findById(userAuthen.getId());
            if (user.isPresent()) {
                user.get().setDefaultLocale(locale);
                userService.save(user.get());
            }
        }
        return this.responseEntity(HttpStatus.OK);
    }

    /**
     * Administrator section
     */

    @PreAuthorize("isHasPermission('user_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request) {
        SearchSpecification<User> specification = new SearchSpecification<>(getSearchCriteriaList());
        return this.responseEntity(userService.findAllWithSearch(specification, getPageable(pageable, User.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('user_manage')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRegisterRequest dto) {
        return this.responseEntity(ceateUserProcess(dto), HttpStatus.CREATED);
    }


    private UserDto ceateUserProcess(UserRegisterRequest dto) {
        User user = new User();
        user.addNew(dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                dto.isActive());

        if (dto.isCheckValidate()) {
            userValidator.validate(user);
        }

        setUserRoles(dto.getSelectedRoles(), user);
        //encrypt pwd
        user.setPassword(encryptService.encrypt(user.getPassword(), user.getSalt()));
        userService.save(user);

        return userService.convertEntityToDto(user);
    }

    private void setUserRoles(Long[] selectedRoles, User user) {
        if (selectedRoles.length > 0) {
            Optional<Role> role;
            for (Long roleId : selectedRoles) {
                role = roleService.findById(roleId);
                role.ifPresent(value -> user.getRoles().add(value));
            }
        }
    }

    @PreAuthorize("isHasPermission('user_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(userService.convertEntityToDto(user.get()), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('user_manage')")
    @PutMapping
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdateRequest dto) {
        Optional<User> user = userService.findById(dto.getId());
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(updateUserProcess(user.get(), dto), HttpStatus.OK);
    }

    private UserDto updateUserProcess(User user, UserUpdateRequest dto) {

        user.update(
                dto.getUsername(),
                dto.getEmail(),
                dto.isActive()
        );

        userValidator.validate(user);
        // delete old permissin for this group
        user.setRoles(new HashSet<>());
        userService.update(user);
        setUserRoles(dto.getSelectedRoles(), user);
        userService.update(user);
        return userService.convertEntityToDto(user);
    }

    @PreAuthorize("isHasPermission('user_manage')")
    @PutMapping("/updateUserPassword/{id}")
    public ResponseEntity<Object> updateUserPassword(@PathVariable("id") Long id, @Valid @RequestBody UserChangePasswordRequest dto) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        updateUserPasswordBase(user.get(), dto);
        return this.responseServerMessage(i18n.getMessage("success.updatePassword"), HttpStatus.OK);
    }

    private void updateUserPasswordBase(User userUpdate, UserChangePasswordRequest dto) {
        userUpdate.setPassword(dto.getPassword());
        //encrypt pwd
        if (!ObjectUtils.isEmpty(userUpdate.getPassword())) {
            userUpdate.setPassword(encryptService.encrypt(userUpdate.getPassword(), userUpdate.getSalt()));
            userService.update(userUpdate);

            if (dto.isLogoutAllDevice()) {
                accessTokenService.revokeTokenByUserId(userUpdate.getId());
            }
        }

    }


    @PreAuthorize("isHasPermission('user_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        deleteUserManage(user.get());
        return this.responseDeleteMessage();
    }


    private void deleteUserManage(User user) {
        userService.delete(user);
    }


    /**
     * User section
     */
    @PutMapping("/updateUserAvatar")
    public ResponseMessage updateUserAvatar(@AuthenticationPrincipal UserDto userAuthen, @RequestParam("fileManagerId") Long fileManagerId) {

        if (userAuthen == null) {
            return new ResponseMessage(HttpStatus.UNAUTHORIZED, null);
        }

        Optional<User> user = userService.findById(userAuthen.getId());
        Optional<FileManager> fileManager = fileManagerService.findById(fileManagerId);
        if (user.isEmpty() || fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        user.get().setAvatarFile(fileManager.get());
        userService.update(user.get());
        return new ResponseMessage(HttpStatus.OK, null);
    }

    @PutMapping("/updateUserCover")
    public ResponseMessage updateUserCover(@AuthenticationPrincipal UserDto userAuthen, @RequestParam("fileManagerId") Long fileManagerId) {

        if (userAuthen == null) {
            return new ResponseMessage(HttpStatus.UNAUTHORIZED, null);
        }

        Optional<User> user = userService.findById(userAuthen.getId());
        Optional<FileManager> fileManager = fileManagerService.findById(fileManagerId);
        if (user.isEmpty() || fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        user.get().setCoverFile(fileManager.get());
        userService.update(user.get());
        return new ResponseMessage(HttpStatus.OK, null);
    }

    @GetMapping("/currentAuthSession")
    public List<AccessTokenDto> currentAuthSession(HttpServletRequest request, @AuthenticationPrincipal UserDto userAuthen, Pageable pageable) {
//        Optional<String> readCookieBy = AppUtil.readCookie(request.getCookies(), ConstantData.COOKIE_JWT_REFRESH_TOKEN);
//        readCookieBy.ifPresent(s -> logger.info("COOKIE_JWT_REFRESH_TOKEN readCookieBy:{}", s));
        return accessTokenService.findAllByUserAndRevoked(userAuthen.getId(), false, pageable);
    }


    @PutMapping("/selfUpdatePassword")
    public ResponseEntity<Object> selfUpdatePassword(@AuthenticationPrincipal UserDto userAuthen, @Valid @RequestBody UserChangePasswordRequest dto) {
        Optional<User> user = userService.findById(userAuthen.getId());
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (ObjectUtils.isEmpty(dto.getNewPassword())) {
            throw new ApiException(new ApiError(
                    HttpStatus.BAD_REQUEST,
                    i18n.getMessage("error.error"),
                    i18n.getMessage("error.error.newPasswordEmpty")));
        }
        //validate pwd strong
        boolean isStrong = AppUtil.validatePasswordStrong(dto.getNewPassword());
        if(!isStrong){
            return this.responseServerMessage(i18n.getMessage("error.pwd.policy.alert"), HttpStatus.BAD_REQUEST);
        }
        if (!encryptService.check(dto.getPassword(), user.get().getPassword()) || !user.get().isActive()) {
            return this.responseServerMessage(i18n.getMessage("error.ondPasswordWrong"), HttpStatus.BAD_REQUEST, false);
        }

        User userUpdate = user.get();
        userUpdate.setPassword(dto.getNewPassword());

        //encrypt pwd
        if (!ObjectUtils.isEmpty(userUpdate.getPassword())) {
            userUpdate.setPassword(encryptService.encrypt(userUpdate.getPassword(), userUpdate.getSalt()));
            userService.update(userUpdate);

            if (dto.isLogoutAllDevice()) {
                accessTokenService.revokeTokenByUserId(user.get().getId());
            }
        }

        return this.responseServerMessage(i18n.getMessage("success.updatePassword"), HttpStatus.OK);
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<Object> updateEmail(@AuthenticationPrincipal UserDto userAuthen, @RequestBody UserPersonalEditRequest dto) {
        Optional<User> user = userService.findById(userAuthen.getId());
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (dto.getEmail() != null) {
            Optional<User> userExist = userService.findByEmail(dto.getEmail());
            if (userExist.isPresent()) {
                if (!Objects.equals(userExist.get().getId(), user.get().getId())) {
                    this.throwError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.unsuccessfull"), i18n.getMessage("error.validateDuplicateEmail", dto.getEmail()));
                }
            }
            user.get().setEmail(dto.getEmail());
        }
        //send email for confirmation before
        userService.update(user.get());
        return this.responseServerMessage(i18n.getMessage("success"), HttpStatus.OK);
    }


}
