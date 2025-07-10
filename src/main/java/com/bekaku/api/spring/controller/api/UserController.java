package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.*;
import com.bekaku.api.spring.enumtype.AppLocale;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.*;
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.validator.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.bekaku.api.spring.util.ConstantData.UNDER_SCORE;

@Slf4j
@RequestMapping(path = "/api/user")
@RestController
@RequiredArgsConstructor
public class UserController extends BaseApiController {
    private final UserService userService;
    private final RoleService roleService;
    private final EncryptService encryptService;
    private final FileManagerService fileManagerService;
    private final AccessTokenService accessTokenService;
    private final UserValidator userValidator;
    private final I18n i18n;
    private final PermissionService permissionService;
    private final ApiClientService apiClientService;
    private final JwtService jwtService;

    @Value("${app.defaults.userpwd}")
    String defaultUserPwd;

    private final JwtProperties jwtProperties;

    private final String SHEET_NAME = "users";

    @GetMapping("/currentUserData")
    public UserDto currentUserData(@AuthenticationPrincipal UserDto userAuthen,
                                   HttpServletRequest request,
                                   @RequestHeader(value = ConstantData.X_USER_ID) String currentUserId) {
//    public UserDto currentUserData(@AuthenticationPrincipal UserDto userAuthen, @CookieValue(name = "jwt_token", required = false) String jwtRefreshTokenCookie) {
        if (userAuthen == null) {
            throw this.responseErrorUnauthorized();
        }
        String jwtRefreshTokenCookie = AppUtil.getCookieByName(request.getCookies(), jwtProperties.getRefreshTokenName() + UNDER_SCORE + currentUserId);
        log.info("currentJwtRefreshTokenCookie :{}", jwtRefreshTokenCookie);
        Optional<User> user = userService.findById(userAuthen.getId());
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        UserDto dto = userService.convertEntityToDto(user.get());
        if (userAuthen.getAccessTokenId() != null) {
            Optional<AccessToken> accessToken = accessTokenService.findById(userAuthen.getAccessTokenId());
            accessToken.ifPresent(token -> dto.setFcmToken(token.getFcmToken()));
        }
        dto.setPermissions(permissionService.findAllPermissionCodeByUserId(userAuthen.getId(), false));
        return dto;
    }

    @GetMapping("/findAllLoginedProfile")
    public List<LoginedProfileItemDto> findAllLoginedProfile(HttpServletRequest request,
                                                             @RequestHeader(value = ConstantData.X_USER_ID) String currentUserId,
                                                             @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {

        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> allJwtRefreshToken = AppUtil.getCookieRefreshJwtTokenAll(request.getCookies(), jwtProperties.getRefreshTokenName() + UNDER_SCORE);
        log.info("allJwtRefreshToken :{}", allJwtRefreshToken);
        if (allJwtRefreshToken.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> filterOutSelfItems = allJwtRefreshToken.stream().filter(s -> s.equalsIgnoreCase(currentUserId)).toList();
        List<LoginedProfileItemDto> items = new ArrayList<>();
        LoginedProfileItemDto itemDto;
        for (String token : filterOutSelfItems) {
            itemDto = getLoginedProfileProcess(token, apiClient.get());
            if (itemDto != null) {
                items.add(itemDto);
            }
        }
        return items;
    }

    private LoginedProfileItemDto getLoginedProfileProcess(String refreshToken, ApiClient apiClient) {
        Optional<String> userUuId = jwtService.getUUIDFromToken(refreshToken, apiClient);
        if (userUuId.isEmpty()) {
            return null;
        }
        Optional<User> user = userService.findByUUID(userUuId.get());
        if (user.isEmpty()) {
            return null;
        }
        UserDto userDto = userService.convertEntityToDto(user.get());
        return new LoginedProfileItemDto(userDto, new NotificationCount());
    }

    @PostMapping("/findLoginedProfile")
    public LoginedProfileItemDto findLoginedProfile(@Valid @RequestBody RefreshTokenRequest dto,
                                                    @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (AppUtil.isEmpty(dto.getRefreshToken())) {
            throw this.responseErrorNotfound();
        }
        LoginedProfileItemDto itemDto = getLoginedProfileProcess(dto.getRefreshToken(), apiClient.get());
        if (itemDto == null) {
            throw this.responseErrorNotfound();
        }

        return itemDto;
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
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdateRequest dto, @PathVariable("id") Long id) {
        Optional<User> user = userService.findById(id);
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
        if (!isStrong) {
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

    @PutMapping("/refreshFcmToken")
    public ResponseEntity<Object> refreshFcmToken(@AuthenticationPrincipal UserDto userAuthen,
                                                  @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        Optional<AccessToken> accessToken = accessTokenService.findByToken(userAuthen.getToken());
        if (accessToken.isPresent() && refreshTokenRequest.getFcmToken() != null) {
            //update null to other device in the same fcm token
            accessTokenService.updateNullFcmToken(refreshTokenRequest.getFcmToken());
            accessToken.get().setFcmToken(refreshTokenRequest.getFcmToken());
            accessTokenService.update(accessToken.get());
        }
        return this.responseEntity(HttpStatus.OK);
    }

    @PutMapping("/updateFcmSetting")
    public ResponseEntity<Object> updateFcmSetting(@AuthenticationPrincipal UserDto userAuthen,
                                                   @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        Optional<AccessToken> accessToken = accessTokenService.findByToken(userAuthen.getToken());
        if (accessToken.isPresent() && refreshTokenRequest.getFcmToken() != null) {
            accessToken.get().setFcmEnable(refreshTokenRequest.isFcmEnable());
            accessTokenService.update(accessToken.get());
        }
        return this.responseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/removeAccessTokenSession")
    public ResponseEntity<Object> removeAccessTokenSession(@AuthenticationPrincipal UserDto userAuthen, @RequestParam(value = "id") Long id
    ) {
        Optional<AccessToken> accessToken = accessTokenService.findById(id);
        if (accessToken.isPresent() && Objects.equals(accessToken.get().getUser().getId(), userAuthen.getId())) {
            accessTokenService.logoutProcess(accessToken.get());
        }
        return this.responseServerMessage(i18n.getMessage("success.logoutSuccess"), HttpStatus.OK);
    }

    @PostMapping("/verifyUserByEmailOrUsername")
    public RefreshTokenResponse verifyUserByEmailOrUsername(@Valid @RequestBody EmailOrUsernameRequest usernameRequest,
                                                            @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);

        if (apiClient.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.OK, i18n.getMessage("error.error"),
                    i18n.getMessage("error.apiClientNotFound")));
        }
        Optional<User> user = userService.findActiveByEmailOrUserName(usernameRequest.getEmailOrUsername());
        RefreshTokenResponse response = new RefreshTokenResponse();
        user.ifPresent(value -> response.setUserId(value.getId()));
        return response;
    }
}
