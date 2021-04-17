package io.beka.service;

import io.beka.exception.AppException;
import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import io.beka.model.User;
import io.beka.repository.AccessTokenRepository;
import io.beka.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    @Value("${jwt.sessionTime}")
    int sessionTime;

    @Value("${jwt.secret}")
    String jwtSecret;

    //Repository
    public AccessToken save(AccessToken accessToken) {
        return accessTokenRepository.save(accessToken);
    }

    @Transactional(readOnly = true)
    public Optional<AccessToken> findById(Long id) {
        return accessTokenRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<AccessToken> findByToken(String token) {
        return accessTokenRepository.findByToken(token);
    }

    public void delete(AccessToken accessToken) {
        accessTokenRepository.delete(accessToken);
    }

    public AccessToken generateRefreshToken(User user, ApiClient apiClient, String userAgent) {
        Date expires = new Date(System.currentTimeMillis() + (sessionTime > 0 ? sessionTime * 1000 : DateUtil.MILLS_IN_YEAR));
        AccessToken accessToken = new AccessToken(
                user,
                userAgent,
                expires,
                false,
                apiClient
        );
        return save(accessToken);
    }

    @Transactional(readOnly = true)
    void validateRefreshToken(String token) {
        accessTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException("Invalid refresh Token"));
    }

    public void deleteRefreshToken(String token) {
        accessTokenRepository.deleteByToken(token);
    }

}
