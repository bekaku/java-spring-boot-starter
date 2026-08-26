package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AccessTokenDto;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.EmailOrUsernameRequest;
import com.bekaku.api.spring.dto.LoginedProfileItemDto;
import com.bekaku.api.spring.dto.NotificationCount;
import com.bekaku.api.spring.dto.RefreshTokenRequest;
import com.bekaku.api.spring.dto.RefreshTokenResponse;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.UserChangePasswordRequest;
import com.bekaku.api.spring.dto.UserPersonalEditRequest;
import com.bekaku.api.spring.dto.UserRegisterRequest;
import com.bekaku.api.spring.dto.UserUpdateRequest;
import com.bekaku.api.spring.enumtype.AppLocale;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppRole;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.AppRoleService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.EncryptService;
import com.bekaku.api.spring.service.FavoriteMenuService;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.ControllerUtil;
import com.bekaku.api.spring.validator.UserValidator;
import jakarta.servlet.http.Cookie;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequestMapping(path = "/api/appUser")
@RestController
@RequiredArgsConstructor
public class AppUserController extends BaseApiController {
    private final AppUserService appUserService;
    private final AppRoleService appRoleService;
    private final EncryptService encryptService;
    private final FileManagerService fileManagerService;
    private final AccessTokenService accessTokenService;
    private final UserValidator userValidator;
    private final I18n i18n;
    private final PermissionService permissionService;
    private final ApiClientService apiClientService;
    private final JwtService jwtService;
    private final FavoriteMenuService favoriteMenuService;

    @Value("${app.defaults.userpwd}")
    String defaultUserPwd;

    private final JwtProperties jwtProperties;

    private final String SHEET_NAME = "users";

    @GetMapping("/currentUserData")
    public ResponseEntity<AppUserDto> currentUserData(@AuthenticationPrincipal AppUserDto userAuthen) {

        if (userAuthen == null) {
            throw this.responseErrorForbidden();
        }

        AppUser user = appUserService.findAndValidateAppUserBy(userAuthen);
        AppUserDto dto = appUserService.convertEntityToDto(user);
        if (userAuthen.getAccessTokenId() != null) {
            Optional<AccessToken> accessToken = accessTokenService.findById(userAuthen.getAccessTokenId());
            accessToken.ifPresent(token -> dto.setFcmToken(token.getFcmToken()));
        }
        dto.setPermissions(permissionService.findAllPermissionCodeByUserId(userAuthen.getId()));
        dto.setFavoriteMenus(favoriteMenuService.findAllByAppUser(user));
        return this.responseEntity(dto, HttpStatus.OK);
    }

    @GetMapping("/findAllLoginedProfile")
    public ResponseEntity<List<LoginedProfileItemDto>> findAllLoginedProfile(HttpServletRequest request,
                                                                             @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {

        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            return this.responseEntity(new ArrayList<>(), HttpStatus.OK);
        }
        List<LoginedProfileItemDto> loginedUsers = new ArrayList<>();

        if (request.getCookies() == null) {
            return this.responseEntity(loginedUsers, HttpStatus.OK);
        }

        String refreshCookiePrefix = jwtProperties.refreshTokenName();

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().startsWith(refreshCookiePrefix)) {

                String refreshToken = cookie.getValue();

                LoginedProfileItemDto profile = getLoginedProfileProcess(refreshToken);

                if (profile != null) {
                    loginedUsers.add(profile);
                }
            }
        }
        return this.responseEntity(loginedUsers, HttpStatus.OK);

    }

    private LoginedProfileItemDto getLoginedProfileProcess(String refreshToken) {
        Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(refreshToken, false);
        if (accessToken.isEmpty()) {
            return null;
        }
        boolean isExpired = accessTokenService.isTokenExpired(accessToken.get());
        if (isExpired) {
            return null;
        }
        AppUserDto appUserDto = appUserService.convertEntityToDto(accessToken.get().getAppUser());
        return new LoginedProfileItemDto(appUserDto, new NotificationCount());
    }


    // refreshToken with jwt format from mobile
    private LoginedProfileItemDto getLoginedProfileProcessApi(String refreshToken, ApiClient apiClient) {
        Optional<String> userUID = jwtService.getUIDFromToken(refreshToken, apiClient);
        if (userUID.isEmpty()) {
            return null;
        }
        Optional<AppUser> user = appUserService.findById(Long.valueOf(userUID.get()));
        if (user.isEmpty()) {
            return null;
        }
        AppUserDto appUserDto = appUserService.convertEntityToDto(user.get());
        return new LoginedProfileItemDto(appUserDto, new NotificationCount());
    }

    @PostMapping("/findLoginedProfile")
    public ResponseEntity<LoginedProfileItemDto> findLoginedProfile(@Valid @RequestBody RefreshTokenRequest dto,
                                                                    @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            return null;
        }
        if (AppUtil.isEmpty(dto.getRefreshToken())) {
            return null;
        }
        LoginedProfileItemDto itemDto = getLoginedProfileProcessApi(dto.getRefreshToken(), apiClient.get());
        if (itemDto == null) {
            return null;
        }

        return this.responseEntity(itemDto, HttpStatus.OK);
    }

    @PutMapping("/updateDefaultLocale")
    public ResponseEntity<?> updateDefaultLocale(@AuthenticationPrincipal AppUserDto userAuthen, @RequestParam("locale") AppLocale locale) {
        if (userAuthen != null) {
            Optional<AppUser> user = appUserService.findById(userAuthen.getId());
            if (user.isPresent()) {
                user.get().setDefaultLocale(locale);
                appUserService.save(user.get());
            }
        }
        return this.responseEntity(HttpStatus.OK);
    }

    /**
     * Administrator section
     */

    @PreAuthorize("@permissionChecker.hasPermission('app_user_list')")
    @GetMapping
    public ResponseEntity<ResponseListDto<AppUserDto>> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<AppUser> specification = ControllerUtil.buildSpecification(request, List.of());
        return this.responseEntity(appUserService.findAllWithSearch(specification, getPageable(pageable, AppUser.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('app_user_add')")
    @PostMapping
    public ResponseEntity<AppUserDto> create(@Valid @RequestBody UserRegisterRequest dto) {
        return this.responseEntity(ceateUserProcess(dto), HttpStatus.CREATED);
    }


    private AppUserDto ceateUserProcess(UserRegisterRequest dto) {
        AppUser appUser = new AppUser();
        appUser.addNew(dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                dto.isActive());

        if (dto.isCheckValidate()) {
            userValidator.validate(appUser);
        }

        setUserImage(dto.getAvatarFileId(), dto.getCoverFileId(), appUser);

        setUserRoles(dto.getSelectedRoles(), appUser);
        //encrypt pwd
        appUser.setPassword(encryptService.encrypt(appUser.getPassword()));
        appUserService.save(appUser);

        return appUserService.convertEntityToDto(appUser);
    }

    private void setUserRoles(Long[] selectedRoles, AppUser appUser) {
        if (selectedRoles.length > 0) {
            Optional<AppRole> role;
            for (Long roleId : selectedRoles) {
                role = appRoleService.findById(roleId);
                role.ifPresent(value -> appUser.getAppRoles().add(value));
            }
        }
    }

    private void setUserImage(Long avatarId, Long coverId, AppUser appUser) {
        if (!AppUtil.isEmpty(avatarId)) {
            Optional<FileManager> avatar = fileManagerService.findById(avatarId);
            avatar.ifPresent(appUser::setAvatarFile);
        }
        if (!AppUtil.isEmpty(coverId)) {
            Optional<FileManager> cover = fileManagerService.findById(coverId);
            cover.ifPresent(appUser::setCoverFile);
        }
    }

    @PreAuthorize("@permissionChecker.hasPermission('app_user_view')")
    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> findOne(@PathVariable("id") Long id) {
        Optional<AppUser> user = appUserService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(appUserService.convertEntityToDto(user.get()), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('app_user_edit')")
    @PutMapping("/{id}")
    public ResponseEntity<AppUserDto> updateUser(@Valid @RequestBody UserUpdateRequest dto, @PathVariable("id") Long id) {
        Optional<AppUser> user = appUserService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(updateUserProcess(user.get(), dto), HttpStatus.OK);
    }

    private AppUserDto updateUserProcess(AppUser appUser, UserUpdateRequest dto) {

        appUser.update(
                dto.getUsername(),
                dto.getEmail(),
                dto.isActive()
        );

        userValidator.validate(appUser);
        // delete old permissin for this group
        appUser.setAppRoles(new HashSet<>());

        setUserImage(dto.getAvatarFileId(), dto.getCoverFileId(), appUser);

        appUserService.update(appUser);
        setUserRoles(dto.getSelectedRoles(), appUser);
        appUserService.update(appUser);
        return appUserService.convertEntityToDto(appUser);
    }

    @PreAuthorize("@permissionChecker.hasPermission('app_user_edit')")
    @PutMapping("/updateUserPassword/{id}")
    public ResponseEntity<?> updateUserPassword(@PathVariable("id") Long id, @Valid @RequestBody UserChangePasswordRequest dto) {
        Optional<AppUser> user = appUserService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        updateUserPasswordBase(user.get(), dto);
        return this.responseServerMessage(i18n.getMessage("success.updatePassword"), HttpStatus.OK);
    }

    private void updateUserPasswordBase(AppUser appUserUpdate, UserChangePasswordRequest dto) {
        appUserUpdate.setPassword(dto.getPassword());
        //encrypt pwd
        if (!ObjectUtils.isEmpty(appUserUpdate.getPassword())) {
            appUserUpdate.setPassword(encryptService.encrypt(appUserUpdate.getPassword()));
            appUserService.update(appUserUpdate);

            if (dto.isLogoutAllDevice()) {
                accessTokenService.revokeTokenByUserId(appUserUpdate.getId());
            }
        }

    }


    @PreAuthorize("@permissionChecker.hasPermission('app_user_delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        Optional<AppUser> user = appUserService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        deleteUserManage(user.get());
        return this.responseDeleteMessage();
    }


    private void deleteUserManage(AppUser appUser) {
        appUserService.delete(appUser);
    }


    /**
     * User section
     */
    @PutMapping("/updateUserAvatar")
    public ResponseEntity<?> updateUserAvatar(@AuthenticationPrincipal AppUserDto userAuthen, @RequestParam("fileManagerId") Long fileManagerId) {

        if (userAuthen == null) {
            return this.responseEntity(HttpStatus.FORBIDDEN);
        }

        Optional<AppUser> user = appUserService.findById(userAuthen.getId());
        Optional<FileManager> fileManager = fileManagerService.findById(fileManagerId);
        if (user.isEmpty() || fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        appUserService.requireTheSameUser(userAuthen.getId(), fileManager.get().getCreatedUser());
        user.get().setAvatarFile(fileManager.get());
        appUserService.update(user.get());
        return this.responseEntity(HttpStatus.OK);
    }

    @PutMapping("/updateUserCover")
    public ResponseEntity<?> updateUserCover(@AuthenticationPrincipal AppUserDto userAuthen, @RequestParam("fileManagerId") Long fileManagerId) {

        if (userAuthen == null) {
            return this.responseEntity(HttpStatus.FORBIDDEN);
        }

        Optional<AppUser> user = appUserService.findById(userAuthen.getId());
        Optional<FileManager> fileManager = fileManagerService.findById(fileManagerId);
        if (user.isEmpty() || fileManager.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        appUserService.requireTheSameUser(userAuthen.getId(), fileManager.get().getCreatedUser());
        user.get().setCoverFile(fileManager.get());
        appUserService.update(user.get());
        return this.responseEntity(HttpStatus.OK);
    }

    @GetMapping("/currentAuthSession")
    public ResponseEntity<List<AccessTokenDto>> currentAuthSession(HttpServletRequest request, @AuthenticationPrincipal AppUserDto userAuthen, Pageable pageable) {
//        Optional<String> readCookieBy = AppUtil.readCookie(request.getCookies(), ConstantData.COOKIE_JWT_REFRESH_TOKEN);
//        readCookieBy.ifPresent(s -> logger.info("COOKIE_JWT_REFRESH_TOKEN readCookieBy:{}", s));
        List<AccessTokenDto> data = accessTokenService.findAllByUserAndRevoked(userAuthen.getId(), false, pageable);
        return this.responseEntity(data, HttpStatus.OK);
    }


    @PutMapping("/selfUpdatePassword")
    public ResponseEntity<?> selfUpdatePassword(@AuthenticationPrincipal AppUserDto userAuthen, @Valid @RequestBody UserChangePasswordRequest dto) {
        Optional<AppUser> user = appUserService.findById(userAuthen.getId());
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

        AppUser appUserUpdate = user.get();
        appUserUpdate.setPassword(dto.getNewPassword());

        //encrypt pwd
        if (!ObjectUtils.isEmpty(appUserUpdate.getPassword())) {
            appUserUpdate.setPassword(encryptService.encrypt(appUserUpdate.getPassword()));
            appUserService.update(appUserUpdate);

            if (dto.isLogoutAllDevice()) {
                accessTokenService.revokeTokenByUserId(user.get().getId());
            }
        }

        return this.responseEntity(HttpStatus.OK);
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<?> updateEmail(@AuthenticationPrincipal AppUserDto userAuthen, @RequestBody UserPersonalEditRequest dto) {
        Optional<AppUser> user = appUserService.findById(userAuthen.getId());
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (dto.getEmail() != null) {
            validateEmail(dto.getEmail(), user.get());
            user.get().setEmail(dto.getEmail());
        }
        //send email for confirmation before
        appUserService.update(user.get());
        return this.responseServerMessage(i18n.getMessage("success"), HttpStatus.OK);
    }

    private void validateEmail(String email, AppUser currentUser) {

        Optional<AppUser> userExist = appUserService.findByEmail(email);
        if (userExist.isPresent()) {
            if (!Objects.equals(userExist.get().getId(), currentUser.getId())) {
                this.throwError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.unsuccessfull"), i18n.getMessage("error.validateDuplicateEmail", email));
            }
        }
    }

    private void validateUsername(String username, AppUser currentUser) {

        Optional<AppUser> userExist = appUserService.findByUsername(username);
        if (userExist.isPresent()) {
            if (!Objects.equals(userExist.get().getId(), currentUser.getId())) {
                this.throwError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.unsuccessfull"), i18n.getMessage("error.validateDuplicateUsername", username));
            }
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<AppUserDto> updateProfile(@AuthenticationPrincipal AppUserDto userAuthen, @RequestBody UserPersonalEditRequest dto) {
        Optional<AppUser> user = appUserService.findById(userAuthen.getId());
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (!AppUtil.isEmpty(dto.getEmail())) {
            validateEmail(dto.getEmail(), user.get());
            user.get().setEmail(dto.getEmail());
        }

        if (!AppUtil.isEmpty(dto.getUsername())) {
            validateUsername(dto.getUsername(), user.get());
            user.get().setUsername(dto.getUsername());
        }

        setUserImage(dto.getAvatarFileId(), dto.getCoverFileId(), user.get());
        //send email for confirmation before
        appUserService.update(user.get());
        return this.responseEntity(appUserService.convertEntityToDto(user.get()), HttpStatus.OK);
    }

    @PutMapping("/refreshFcmToken")
    public ResponseEntity<?> refreshFcmToken(@AuthenticationPrincipal AppUserDto userAuthen,
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
    public ResponseEntity<?> updateFcmSetting(@AuthenticationPrincipal AppUserDto userAuthen,
                                              @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        Optional<AccessToken> accessToken = accessTokenService.findByToken(userAuthen.getToken());
        if (accessToken.isPresent() && refreshTokenRequest.getFcmToken() != null) {
            accessToken.get().setFcmEnable(refreshTokenRequest.isFcmEnable());
            accessTokenService.update(accessToken.get());
        }
        return this.responseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/removeAccessTokenSession")
    public ResponseEntity<?> removeAccessTokenSession(@AuthenticationPrincipal AppUserDto userAuthen, @RequestParam(value = "id") Long id
    ) {
        Optional<AccessToken> accessToken = accessTokenService.findById(id);
        if (accessToken.isPresent() && Objects.equals(accessToken.get().getAppUser().getId(), userAuthen.getId())) {
            accessTokenService.logoutProcess(accessToken.get());
        }
        return this.responseServerMessage(i18n.getMessage("success.logoutSuccess"), HttpStatus.OK);
    }

    @PostMapping("/verifyUserByEmailOrUsername")
    public ResponseEntity<RefreshTokenResponse> verifyUserByEmailOrUsername(@Valid @RequestBody EmailOrUsernameRequest usernameRequest,
                                                                            @RequestHeader(value = ConstantData.ACCEPT_APIC_LIENT) String apiClientName) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);

        if (apiClient.isEmpty()) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"),
                    i18n.getMessage("error.apiClientNotFound")));
        }
        Optional<AppUser> user = appUserService.findActiveByEmailOrUserName(usernameRequest.getEmailOrUsername());
        RefreshTokenResponse response = new RefreshTokenResponse();
        user.ifPresent(value -> response.setUserId(value.getId()));
        return this.responseEntity(response, HttpStatus.OK);
    }
}
