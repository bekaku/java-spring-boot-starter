package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.LoginRequest;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.dto.RefreshTokenResponse;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.vo.IpAddress;

public interface AuthService {


    AppUser getCurrentUser();

    void fetchUserAndEnable(AccessToken verificationToken);

    RefreshTokenResponse login(AppUser appUser, LoginRequest loginRequest, ApiClient apiClient, String userAgent, IpAddress ipAddress);

    RefreshTokenResponse refreshToken(AccessToken accessToken, ApiClient apiClient, String userAgent);

    void verifyAccount(String token);
}
