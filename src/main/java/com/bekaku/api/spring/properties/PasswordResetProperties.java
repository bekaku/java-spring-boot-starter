package com.bekaku.api.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.password-reset")
public record PasswordResetProperties(
        Duration codeExpiry,        // app.password-reset.code-expiry
        Duration resendCooldown,    // app.password-reset.resend-cooldown
        int maxFailedAttempts,      // app.password-reset.max-failed-attempts
        Duration verifyResetWindow  // app.password-reset.verify-reset-window
) {
    public PasswordResetProperties {
        codeExpiry = codeExpiry == null ? Duration.ofMinutes(15) : codeExpiry;
        resendCooldown = resendCooldown == null ? Duration.ofSeconds(60) : resendCooldown;
        maxFailedAttempts = maxFailedAttempts <= 0 ? 5 : maxFailedAttempts;
        verifyResetWindow = verifyResetWindow == null ? Duration.ofMinutes(5) : verifyResetWindow;
    }
}
