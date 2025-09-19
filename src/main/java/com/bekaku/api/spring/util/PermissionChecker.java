package com.bekaku.api.spring.util;


import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("permissionChecker")
public class PermissionChecker {


    private final AuthenticationHelper authHelper;
    private final PermissionService permissionService;

    public PermissionChecker(AuthenticationHelper authHelper, PermissionService permissionService) {
        this.authHelper = authHelper;
        this.permissionService = permissionService;
    }

    /*
            @PreAuthorize("@permissionChecker.hasPermission('file_manager_view')")
         */
    public boolean hasPermission(String permission) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) return false;
//        AppUserDto user = (AppUserDto) authentication.getPrincipal();
//        if (user == null) return false;
        Long userID = authHelper.getAuthenticatedUser();
        if (userID == null) return false;
        log.info("PermissionChecker > hasPermission > permisson:{}, userID:{}", permission, userID);
        return validatePermit(userID, permission);
    }

    /*
    @PreAuthorize("@permissionChecker.hasAnyPermission('file_manager_admin', 'file_manager_delete')")
     */
    public boolean hasAnyPermission(String... permissions) {

        Long userID = authHelper.getAuthenticatedUser();
        if (userID == null) return false;
        log.info("PermissionChecker > hasAnyPermission > permissions:{}, userID:{}", permissions, userID);
        for (String permission : permissions) {
            if (validatePermit(userID, permission)) {
                return true;
            }
        }
        return false;
    }

    /*
    @PreAuthorize("@permissionChecker.hasAllPermissions('file_manager_admin', 'file_manager_delete')")
     */
    public boolean hasAllPermissions(String... permissions) {
        Long userID = authHelper.getAuthenticatedUser();
        if (userID == null) return false;
        log.info("PermissionChecker > hasAllPermissions > permissions:{}, userID:{}", permissions, userID);
        for (String permission : permissions) {
            if (!validatePermit(userID, permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean validatePermit(Long userId, String permission) {
        return permissionService.isHasPermission(userId, permission);
    }
}
