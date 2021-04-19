package io.beka.serviceImpl;

import io.beka.dto.AuthenticationResponse;
import io.beka.dto.LoginRequest;
import io.beka.dto.RefreshTokenRequest;
import io.beka.exception.AppException;
import io.beka.model.AccessToken;
import io.beka.model.ApiClient;
import io.beka.model.User;
import io.beka.repository.UserRepository;
import io.beka.service.AccessTokenService;
import io.beka.service.AuthService;
import io.beka.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AccessTokenService accessTokenService;

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    @Override
    public void fetchUserAndEnable(AccessToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException("User not found with name - " + username));
        user.setStatus(true);
        userRepository.save(user);
    }

    @Override
    public AuthenticationResponse login(User user, LoginRequest loginRequest, ApiClient apiClient, String userAgent) {
        String token = accessTokenService.generateRefreshToken(user, apiClient, userAgent).getToken();
        return AuthenticationResponse.builder()
                .authenticationToken(jwtService.toToken(token, apiClient))
                .refreshToken(token)
                .expiresAt(Instant.now().plusMillis(jwtService.expireMillisec()))
                .email(loginRequest.getEmail())
                .image(user.getImage())
                .username(user.getUsername())
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest, ApiClient apiClient, String userAgent) {
        AccessToken accessToken = accessTokenService.findByToken(refreshTokenRequest.getRefreshToken()).orElseThrow(() -> new AppException("Token not found with name - " + refreshTokenRequest.getRefreshToken()));

        //revoke old token
//        accessTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
//        accessToken.setRevoked(true);
//        accessTokenService.save(accessToken);

        User user = userRepository.findByEmail(refreshTokenRequest.getEmail()).orElseThrow(() -> new AppException("User not found with name - " + refreshTokenRequest.getEmail()));
//        String token = accessTokenService.generateRefreshToken(user, apiClient, userAgent).getToken();

        //update refresh token
        String token = UUID.randomUUID().toString();
        accessToken.setToken(token);
        accessToken.setExpiresAt(jwtService.expireTimeFromNow());
        accessTokenService.save(accessToken);

        return AuthenticationResponse.builder()
                .authenticationToken(jwtService.toToken(token, apiClient))
                .refreshToken(token)
                .expiresAt(Instant.now().plusMillis(jwtService.expireMillisec()))
                .email(refreshTokenRequest.getEmail())
                .image(user.getImage())
                .username(user.getUsername())
                .build();
    }

    @Override
    public void verifyAccount(String token) {
        Optional<AccessToken> verificationToken = accessTokenService.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new AppException("Invalid Token")));
    }
}
