package io.beka.service;

import io.beka.dto.AuthenticationResponse;
import io.beka.dto.LoginRequest;
import io.beka.dto.RefreshTokenRequest;
import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import io.beka.model.User;

public interface AuthService {


    User getCurrentUser();

    void fetchUserAndEnable(AccessToken verificationToken);

    AuthenticationResponse login(User user, LoginRequest loginRequest, ApiClient apiClient, String userAgent);

    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest, ApiClient apiClient, String userAgent);

    void verifyAccount(String token);
}
