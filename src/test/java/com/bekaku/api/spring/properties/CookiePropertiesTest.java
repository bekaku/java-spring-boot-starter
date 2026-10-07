package com.bekaku.api.spring.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CookiePropertiesTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void blankDomainMeansHostOnlyCookies(String raw) {
        CookieProperties props = new CookieProperties(true, "Lax", raw);

        assertThat(props.domain()).isNull();
        assertThat(props.hasDomain()).isFalse();
    }

    @Test
    void twoArgConstructorKeepsHostOnlyCookies() {
        CookieProperties props = new CookieProperties(false, "Lax");

        assertThat(props.hasDomain()).isFalse();
        assertThat(props.secure()).isFalse();
        assertThat(props.sameSite()).isEqualTo("Lax");
    }

    @ParameterizedTest
    @ValueSource(strings = {"example.com", ".example.com", "  .Example.COM  ", "api.example.co.th"})
    void acceptsHostnameWithOrWithoutLeadingDot(String raw) {
        CookieProperties props = new CookieProperties(true, "Lax", raw);

        assertThat(props.hasDomain()).isTrue();
        assertThat(props.domain()).doesNotStartWith(".").isEqualTo(raw.trim().toLowerCase().replaceFirst("^\\.", ""));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com", "example.com/path", "example.com:8080", "*.example.com",
            "exa mple.com", "localhost", "192.168.1.10", "..example.com", "example..com",
            "-example.com", "example_x.com", "[::1]"})
    void rejectsValuesThatAreNotABareHostname(String raw) {
        assertThatThrownBy(() -> new CookieProperties(true, "Lax", raw))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("app.cookie.domain");
    }

    // the real startup path: relaxed binding through the constructor with two constructors present
    @Test
    void bindsFromConfigurationProperties() {
        Binder binder = new Binder(new MapConfigurationPropertySource(Map.of(
                "app.cookie.secure", "true", "app.cookie.same-site", "None", "app.cookie.domain", ".example.com")));

        CookieProperties props = binder.bind("app.cookie", CookieProperties.class).get();

        assertThat(props).isEqualTo(new CookieProperties(true, "None", "example.com"));
    }

    @Test
    void emptyDomainPlaceholderBindsAsHostOnly() {
        Binder binder = new Binder(new MapConfigurationPropertySource(Map.of(
                "app.cookie.secure", "true", "app.cookie.same-site", "Lax", "app.cookie.domain", "")));

        assertThat(binder.bind("app.cookie", CookieProperties.class).get().hasDomain()).isFalse();
    }

    @Test
    void invalidDomainFailsBindingWithAMessageNamingTheProperty() {
        Binder binder = new Binder(new MapConfigurationPropertySource(Map.of(
                "app.cookie.secure", "true", "app.cookie.same-site", "Lax", "app.cookie.domain", "https://example.com")));

        assertThatThrownBy(() -> binder.bind("app.cookie", CookieProperties.class))
                .hasStackTraceContaining("app.cookie.domain");
    }
}
