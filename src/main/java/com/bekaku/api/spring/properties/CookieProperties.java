package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Locale;
import java.util.regex.Pattern;

@ConfigurationProperties(prefix = "app.cookie")
public record CookieProperties(
        boolean secure,
        String sameSite,
        String domain       // app.cookie.domain (env APP_COOKIE_DOMAIN); blank = host-only cookies
) {

    // dot-separated DNS labels, at least two (a single label such as "localhost" is rejected by browsers as a cookie Domain)
    private static final Pattern HOSTNAME = Pattern.compile(
            "^(?=.{1,253}$)[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?)+$");
    private static final Pattern NUMERIC_TLD = Pattern.compile("^\\d+$");

    @ConstructorBinding
    public CookieProperties {
        domain = normalizeDomain(domain);
    }

    /**
     * Keeps the pre-existing two-argument shape: host-only cookies (no Domain attribute).
     */
    public CookieProperties(boolean secure, String sameSite) {
        this(secure, sameSite, null);
    }

    public boolean hasDomain() {
        return domain != null;
    }

    /**
     * Blank → {@code null} (host-only). Otherwise trims, lower-cases and drops one leading dot,
     * because a leading dot is optional in RFC 6265 and browsers ignore it. Throws when the value is
     * not a bare hostname (scheme, port, path, wildcard, whitespace, IP address).
     */
    static String normalizeDomain(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.startsWith(".")) {
            value = value.substring(1);
        }
        if (!HOSTNAME.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid app.cookie.domain '" + raw
                    + "': use a bare hostname such as example.com or .example.com (no scheme, port, path, wildcard, whitespace, or single-label host)");
        }
        String tld = value.substring(value.lastIndexOf('.') + 1);
        if (NUMERIC_TLD.matcher(tld).matches()) {
            throw new IllegalArgumentException("Invalid app.cookie.domain '" + raw
                    + "': an IP address cannot be used as a cookie domain");
        }
        return value;
    }
}
