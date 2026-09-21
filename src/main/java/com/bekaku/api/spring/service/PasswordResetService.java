package com.bekaku.api.spring.service;

public interface PasswordResetService {

    /**
     * Always succeeds from the caller's point of view - eligibility, cooldown and mail
     * delivery are handled internally so the public response never reveals account state.
     */
    void requestReset(String email);

    void verifyCode(String email, String code);

    void resetPassword(String email, String code, String newPassword);
}
