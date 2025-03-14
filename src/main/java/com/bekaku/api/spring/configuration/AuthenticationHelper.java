package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.vo.IpAddress;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationHelper {
    public Long getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDto userDetails) {
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
}
