package com.bekaku.api.spring.util;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.vo.IpAddress;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final CookieUtil cookieUtil;
    private final JwtService jwtService;

    public Long getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AppUserDto userDetails) {
                return userDetails.getId(); // Use getId() if your custom user has it
            }
        }
        return null;
    }

    public String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "Unknown"; // No request context available
        }

        HttpServletRequest request = attributes.getRequest();
//        String ip = request.getHeader("X-Forwarded-For"); // Check for proxy headers
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr(); // Fallback to direct IP
//        }
//        return ip;
        IpAddress ipAddress = AppUtil.getIpaddress(request);
        if (ipAddress == null) {
            return "Unknown";
        }
        return ipAddress.getIp();
    }

    public Optional<String> getAccessToken(HttpServletRequest request) {
        Optional<String> jwtToken = Optional.ofNullable(cookieUtil.getCurrentUserAccessToken(request));
        if (jwtToken.isEmpty()) {
            jwtToken = jwtService.getSubFromAuthorizationHeader(request.getHeader(ConstantData.AUTHORIZATION), null);
        }
        return jwtToken;
    }
}
