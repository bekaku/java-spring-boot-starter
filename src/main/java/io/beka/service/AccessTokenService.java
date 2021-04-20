package io.beka.service;

import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import io.beka.model.User;

import java.util.Optional;

public interface AccessTokenService extends BaseService<AccessToken, AccessToken> {
    Optional<AccessToken> findByToken(String token);

    AccessToken generateRefreshToken(User user, ApiClient apiClient, String userAgent);

    void validateRefreshToken(String token);

    void deleteRefreshToken(String token);
}
