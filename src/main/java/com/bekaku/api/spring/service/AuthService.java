package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.LoginRequest;
import com.bekaku.api.spring.dto.RefreshTokenRequest;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.dto.RefreshTokenResponse;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.vo.IpAddress;

public interface AuthService {


    User getCurrentUser();

    void fetchUserAndEnable(AccessToken verificationToken);

    RefreshTokenResponse login(User user, LoginRequest loginRequest, ApiClient apiClient, String userAgent, IpAddress ipAddress);

    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest, ApiClient apiClient, String userAgent);

    void verifyAccount(String token);
}
