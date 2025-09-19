package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

//    @Autowired
//    private PermissionService permissionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
//        @PreAuthorize("hasPermission(null, 'file_manager_create')")
//        if (authentication == null) return false;
//        AppUserDto user = (AppUserDto) authentication.getPrincipal();
//        if (user == null) return false;
        System.out.println(">>> Using CustomPermissionEvaluator > hasPermission 1");
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
//        @PreAuthorize("hasPermission(#fileId, 'file', 'file_manager_edit')")
        System.out.println(">>> Using CustomPermissionEvaluator > hasPermission 2");
        return false;
    }

}
