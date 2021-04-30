package io.beka.serviceImpl;

import io.beka.configuration.I18n;
import io.beka.dto.AuthenticationResponse;
import io.beka.dto.LoginRequest;
import io.beka.dto.RefreshTokenRequest;
import io.beka.exception.ApiError;
import io.beka.exception.ApiException;
import io.beka.exception.AppException;
import io.beka.model.*;
import io.beka.repository.UserRepository;
import io.beka.service.*;
import io.beka.util.AppUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AccessTokenService accessTokenService;
    private final LoginLogService loginLogService;

    private final I18n i18n;

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    @Override
    public void fetchUserAndEnable(AccessToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userService.findByUsername(username).orElseThrow(() -> new AppException("User not found with name - " + username));
        user.setStatus(true);
        userService.save(user);
    }

    @Override
    public AuthenticationResponse login(User user, LoginRequest loginRequest, ApiClient apiClient, String userAgent) {
        AccessToken token = accessTokenService.generateRefreshToken(user, apiClient, userAgent, new LoginLog(loginRequest.getLoginForm(), user, AppUtil.getIpaddress()));
//        loginLogService.save(new LoginLog(loginRequest.getLoginForm(), user, AppUtil.getIpaddress()));
        return AuthenticationResponse.builder()
                .authenticationToken(jwtService.toToken(token.getToken(), apiClient))
                .refreshToken(token.getToken())
                .expiresAt(Instant.now().plusMillis(jwtService.expireMillisec()))
                .email(loginRequest.getEmail())
                .image(user.getImage())
                .username(user.getUsername())
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest, ApiClient apiClient, String userAgent) {
        AccessToken accessToken = accessTokenService.findByToken(refreshTokenRequest.getRefreshToken()).orElseThrow(
                () -> new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"),
                        i18n.getMessage("error.tokenNOtFound", refreshTokenRequest.getRefreshToken()))));
        User user = userService.findByEmail(refreshTokenRequest.getEmail()).orElseThrow(
                () -> new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"),
                        i18n.getMessage("error.userNotFoundWithEmail", refreshTokenRequest.getEmail()))));

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
