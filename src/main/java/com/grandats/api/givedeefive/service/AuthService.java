package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.LoginRequest;
import com.grandats.api.givedeefive.dto.RefreshTokenRequest;
import com.grandats.api.givedeefive.model.User;
import com.grandats.api.givedeefive.dto.RefreshTokenResponse;
import com.grandats.api.givedeefive.model.AccessToken;
import com.grandats.api.givedeefive.model.ApiClient;

public interface AuthService {


    User getCurrentUser();

    void fetchUserAndEnable(AccessToken verificationToken);

    RefreshTokenResponse login(User user, LoginRequest loginRequest, ApiClient apiClient, String userAgent);

    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest, ApiClient apiClient, String userAgent);

    void verifyAccount(String token);
}
