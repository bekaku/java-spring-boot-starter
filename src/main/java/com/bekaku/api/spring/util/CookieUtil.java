package com.bekaku.api.spring.util;


import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.CookieProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final AppProperties appProperties;

    public ResponseCookie setCookie(String cookieName, String value, Duration duration, String path, boolean httponly) {
        if (AppUtil.isEmpty(cookieName)) {
            return null;
        }
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, value)
                .httpOnly(httponly)
                .secure(appProperties.cookie().secure()) // true in prod (HTTPS), false in dev
                .path(path)
                .sameSite(appProperties.cookie().sameSite()) // "Lax" for dev; "None" + secure for prod
                .maxAge(duration);
        applyDomain(builder);
        return builder.build();
    }

    public ResponseCookie clearCookie(String cookieName, String path, boolean httponly) {
        if (AppUtil.isEmpty(cookieName)) {
            return null;
        }
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, "")
                .httpOnly(httponly)
                .secure(appProperties.cookie().secure())
                .path(path)
                .maxAge(0) // Deletes cookie
                .sameSite(appProperties.cookie().sameSite()); // Must match how it was originally set
        applyDomain(builder); // Domain must match too, otherwise the browser keeps the cookie
        return builder.build();
    }

    // app.cookie.domain blank → no Domain attribute (host-only cookie, the default)
    private void applyDomain(ResponseCookie.ResponseCookieBuilder builder) {
        CookieProperties cookie = appProperties.cookie();
        if (cookie.hasDomain()) {
            builder.domain(cookie.domain());
        }
    }

    // A Domain that does not cover the API host is not an error (the frontend host may differ), but it is
    // the usual sign of a typo, so make it visible at startup.
    @PostConstruct
    void warnWhenDomainDoesNotCoverAppUrl() {
        CookieProperties cookie = appProperties.cookie();
        if (cookie == null || !cookie.hasDomain() || AppUtil.isEmpty(appProperties.url())) {
            return;
        }
        String host;
        try {
            host = URI.create(appProperties.url()).getHost();
        } catch (IllegalArgumentException e) {
            return;
        }
        if (host != null && !host.equalsIgnoreCase(cookie.domain()) && !host.toLowerCase().endsWith("." + cookie.domain())) {
            log.warn("app.cookie.domain '{}' does not cover the host of app.url '{}': browsers will drop the auth cookies the API sets",
                    cookie.domain(), host);
        }
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
