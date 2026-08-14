package com.bekaku.api.spring.util;


import com.bekaku.api.spring.properties.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final AppProperties appProperties;

    public ResponseCookie setCookie(String cookieName, String value, Duration duration, String path, boolean httponly) {
        if (AppUtil.isEmpty(cookieName)) {
            return null;
        }
        return ResponseCookie.from(cookieName, value)
                .httpOnly(httponly)
                .secure(appProperties.cookie().secure()) // true in prod (HTTPS), false in dev
                .path(path)
                .sameSite(appProperties.cookie().sameSite()) // "Lax" for dev; "None" + secure for prod
                .maxAge(duration)
                .build();
    }

    public ResponseCookie clearCookie(String cookieName, String path, boolean httponly) {
        if (AppUtil.isEmpty(cookieName)) {
            return null;
        }
        return ResponseCookie.from(cookieName, "")
                .httpOnly(httponly)
                .secure(appProperties.cookie().secure())
                .path(path)
                .maxAge(0) // Deletes cookie
                .sameSite(appProperties.cookie().sameSite()) // Must match how it was originally set
                .build();
    }

    public String getCurrentUserID(HttpServletRequest request){
        return AppUtil.getCookieByName(request.getCookies(), appProperties.jwt().currentUserKey());
    }

    public String getCurrentUserRefreshToken(HttpServletRequest request){
        String uid = getCurrentUserID(request);
        if(AppUtil.isEmpty(uid)){
            return null;
        }
        return AppUtil.getCookieByName(request.getCookies(), appProperties.jwt().refreshTokenName() + uid);
    }

    public String getCurrentUserAccessToken(HttpServletRequest request){
        String uid = getCurrentUserID(request);
        if(AppUtil.isEmpty(uid)){
            return null;
        }
        return AppUtil.getCookieByName(request.getCookies(), appProperties.jwt().tokenName() + uid);
    }

}
