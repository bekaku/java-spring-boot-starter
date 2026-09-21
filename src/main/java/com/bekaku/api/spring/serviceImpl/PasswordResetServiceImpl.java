package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.properties.PasswordResetProperties;
import com.bekaku.api.spring.repository.AccessTokenRepository;
import com.bekaku.api.spring.repository.AppUserRepository;
import com.bekaku.api.spring.service.EmailService;
import com.bekaku.api.spring.service.EncryptService;
import com.bekaku.api.spring.service.PasswordResetService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AccessTokenRepository accessTokenRepository;
    private final AppUserRepository appUserRepository;
    private final EmailService emailService;
    private final EncryptService encryptService;
    private final PasswordResetProperties passwordResetProperties;
    private final I18n i18n;

    @Transactional
    @Override
    public void requestReset(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null) {
            return;
        }
        Optional<AppUser> userOpt = appUserRepository.findByEmail(normalizedEmail);
        if (userOpt.isEmpty() || !userOpt.get().isActive()) {
            return;
        }
        AppUser user = userOpt.get();
        AccessToken token = accessTokenRepository
                .findLatestAccessTokenByUserForUpdate(user, AccessTokenServiceType.FORGOT_PASSWORD)
                .orElse(null);
        LocalDateTime now = DateUtil.getLocalDateTimeNow();
        if (token != null && token.getCreatedDate() != null
                && Duration.between(token.getCreatedDate(), now).compareTo(passwordResetProperties.resendCooldown()) < 0) {
            // still within the resend cooldown - report success without touching the token or sending mail
            return;
        }
        if (token == null) {
            token = new AccessToken();
        }
        String rawCode = AppUtil.generateRandomNumber(6);
        Date expiresAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant().plus(passwordResetProperties.codeExpiry()));
        token.onCreateToken(user, expiresAt, rawCode, AccessTokenServiceType.FORGOT_PASSWORD);
        AccessToken saved = accessTokenRepository.save(token);
        try {
            emailService.sendEmailRecoveryToken(saved);
        } catch (Exception e) {
            log.warn("Password reset email delivery failed for tokenId={}: {}: {}",
                    saved.getId(), e.getClass().getName(), e.getMessage());
            // the raw code never reached the user - invalidate so the next request can retry immediately
            accessTokenRepository.delete(saved);
        }
    }

    @Transactional
    @Override
    public void verifyCode(String email, String code) {
        AppUser user = requireEligibleUser(email);
        if (AppUtil.isEmpty(code)) {
            throw invalidCodeError();
        }
        AccessToken token = lockActiveToken(user);
        if (!matches(code, token)) {
            registerFailure(token);
            throw invalidCodeError();
        }
        token.setVerifiedAt(DateUtil.getLocalDateTimeNow());
        accessTokenRepository.save(token);
    }

    @Transactional
    @Override
    public void resetPassword(String email, String code, String newPassword) {
        if (!AppUtil.validatePasswordStrong(newPassword)) {
            throw weakPasswordError();
        }
        AppUser user = requireEligibleUser(email);
        if (AppUtil.isEmpty(code)) {
            throw invalidCodeError();
        }
        AccessToken token = lockActiveToken(user);
        if (!matches(code, token)) {
            registerFailure(token);
            throw invalidCodeError();
        }
        LocalDateTime now = DateUtil.getLocalDateTimeNow();
        if (token.getVerifiedAt() == null
                || Duration.between(token.getVerifiedAt(), now).compareTo(passwordResetProperties.verifyResetWindow()) > 0) {
            throw invalidCodeError();
        }
        String encrypted = encryptService.encrypt(newPassword);
        appUserRepository.updatePasswordBy(user, encrypted);
        token.setConsumedAt(now);
        token.setRevoked(true);
        accessTokenRepository.save(token);
        accessTokenRepository.revokeTokenByUserIdAndService(user.getId(), AccessTokenServiceType.LOGIN);
    }

    private AppUser requireEligibleUser(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null) {
            throw invalidCodeError();
        }
        return appUserRepository.findByEmail(normalizedEmail).orElseThrow(this::invalidCodeError);
    }

    private AccessToken lockActiveToken(AppUser user) {
        AccessToken token = accessTokenRepository
                .findLatestAccessTokenByUserForUpdate(user, AccessTokenServiceType.FORGOT_PASSWORD)
                .orElseThrow(this::invalidCodeError);
        if (token.isRevoked() || token.getConsumedAt() != null) {
            throw invalidCodeError();
        }
        if (token.getFailedAttempts() >= passwordResetProperties.maxFailedAttempts()) {
            throw invalidCodeError();
        }
        if (isExpired(token)) {
            throw invalidCodeError();
        }
        return token;
    }

    private boolean isExpired(AccessToken token) {
        LocalDateTime expiresAt = DateUtil.convertDateToLacalDatetime(token.getExpiresAt());
        return expiresAt == null || DateUtil.isBefore(expiresAt, DateUtil.getLocalDateTimeNow());
    }

    private void registerFailure(AccessToken token) {
        token.setFailedAttempts(token.getFailedAttempts() + 1);
        if (token.getFailedAttempts() >= passwordResetProperties.maxFailedAttempts()) {
            token.setRevoked(true);
        }
        accessTokenRepository.save(token);
    }

    private boolean matches(String code, AccessToken token) {
        return MessageDigest.isEqual(
                HashUtil.sha256(code).getBytes(StandardCharsets.UTF_8),
                token.getToken().getBytes(StandardCharsets.UTF_8));
    }

    private String normalizeEmail(String email) {
        if (AppUtil.isEmpty(email)) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private ApiException invalidCodeError() {
        return new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"),
                i18n.getMessage("error.verify.code.wrong")));
    }

    private ApiException weakPasswordError() {
        return new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"),
                i18n.getMessage("error.pwd.policy.alert")));
    }
}
