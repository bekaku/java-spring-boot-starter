package com.grandats.api.givedeefive.serviceImpl;

import com.grandats.api.givedeefive.configuration.I18n;
import com.grandats.api.givedeefive.dto.LoginRequest;
import com.grandats.api.givedeefive.dto.RefreshTokenRequest;
import com.grandats.api.givedeefive.dto.RefreshTokenResponse;
import com.grandats.api.givedeefive.model.User;
import com.grandats.api.givedeefive.service.*;
import com.grandats.api.givedeefive.exception.ApiError;
import com.grandats.api.givedeefive.exception.ApiException;
import com.grandats.api.givedeefive.exception.AppException;
import com.grandats.api.givedeefive.model.AccessToken;
import com.grandats.api.givedeefive.model.ApiClient;
import com.grandats.api.givedeefive.model.LoginLog;
import com.grandats.api.givedeefive.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FileManagerService fileManagerService;

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
        user.setActive(true);
        userService.save(user);
    }

    @Override
    public RefreshTokenResponse login(User user, LoginRequest loginRequest, ApiClient apiClient, String userAgent) {
        LoginLog loginLog = loginLogService.save(new LoginLog(loginRequest.getLoginFrom(), user, AppUtil.getIpaddress()));
        AccessToken token = accessTokenService.generateRefreshToken(user, apiClient, userAgent, loginLog, loginRequest.getFcmToken());

//        AuthenticationResponse response = AuthenticationResponse.builder()
//                .authenticationToken(jwtService.toToken(token.getToken(), apiClient))
//                .refreshToken(token.getToken())
//                .expiresAt(jwtService.expireTimeFromNow())
//                .email(loginRequest.getEmail())
//                .username(user.getUsername())
//                .build();
//        Optional<ImageDto> imageDto = fileManagerService.findImageDtoBy(user.getAvatarFile());
//        imageDto.ifPresent(response::setAvatar);
        return RefreshTokenResponse.builder()
                .userId(user.getId())
                .authenticationToken(jwtService.toToken(token.getToken(), apiClient))
                .refreshToken(token.getToken())
                .expiresAt(jwtService.expireTimeFromNow())
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest, ApiClient apiClient, String userAgent) {
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

        return RefreshTokenResponse.builder()
                .authenticationToken(jwtService.toToken(token, apiClient))
                .refreshToken(token)
                .expiresAt(jwtService.expireTimeFromNow())
                .build();
    }

    @Override
    public void verifyAccount(String token) {
        Optional<AccessToken> verificationToken = accessTokenService.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new AppException("Invalid Token")));
    }
}
