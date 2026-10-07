package com.bekaku.api.spring.util;

import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.CookieProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CookieUtilTest {

    private CookieUtil cookieUtil(String domain) {
        AppProperties appProperties = mock(AppProperties.class);
        when(appProperties.cookie()).thenReturn(new CookieProperties(true, "Lax", domain));
        return new CookieUtil(appProperties);
    }

    @Test
    void setCookieWithoutDomainStaysHostOnly() {
        ResponseCookie cookie = cookieUtil(null).setCookie("_at1", "token", Duration.ofMinutes(15), "/", true);

        assertThat(cookie.getDomain()).isNull();
        assertThat(cookie.toString()).doesNotContain("Domain=")
                .contains("Path=/", "HttpOnly", "Secure", "SameSite=Lax", "Max-Age=900");
    }

    @Test
    void clearCookieWithoutDomainStaysHostOnly() {
        ResponseCookie cookie = cookieUtil("").clearCookie("_at1", "/", true);

        assertThat(cookie.toString()).doesNotContain("Domain=").contains("Max-Age=0", "Path=/", "HttpOnly");
    }

    @Test
    void setCookieCarriesConfiguredDomain() {
        ResponseCookie cookie = cookieUtil(".Example.com").setCookie("_rt1", "refresh", Duration.ofDays(7), "/", true);

        assertThat(cookie.getDomain()).isEqualTo("example.com");
        assertThat(cookie.toString()).contains("Domain=example.com", "Path=/", "HttpOnly", "Secure", "SameSite=Lax");
    }

    @Test
    void clearCookieCarriesTheSameDomainAsSetCookie() {
        CookieUtil util = cookieUtil("example.com");

        ResponseCookie set = util.setCookie("_cuid", "1", Duration.ofDays(7), "/", true);
        ResponseCookie clear = util.clearCookie("_cuid", "/", true);

        assertThat(clear.getDomain()).isEqualTo(set.getDomain()).isEqualTo("example.com");
        assertThat(clear.toString()).contains("Domain=example.com", "Max-Age=0");
    }

    @Test
    void blankCookieNameProducesNoCookie() {
        CookieUtil util = cookieUtil("example.com");

        assertThat(util.setCookie("", "x", Duration.ofDays(1), "/", true)).isNull();
        assertThat(util.clearCookie(null, "/", true)).isNull();
    }

    @Test
    void startupWarningNeverThrowsForAnyAppUrl() {
        for (String url : new String[]{null, "", "not a url", "https://api.example.com", "https://other.org", "http://localhost"}) {
            AppProperties appProperties = mock(AppProperties.class);
            when(appProperties.cookie()).thenReturn(new CookieProperties(true, "Lax", "example.com"));
            when(appProperties.url()).thenReturn(url);

            new CookieUtil(appProperties).warnWhenDomainDoesNotCoverAppUrl();
        }
    }
}
