package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.LoginRequest;
import com.bekaku.api.spring.dto.RefreshTokenRequest;
import com.bekaku.api.spring.dto.RefreshTokenResponse;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.exception.AppException;
import com.bekaku.api.spring.model.*;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.vo.IpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private FileManagerService fileManagerService;

    private UserAgentService userAgentService;
    @Autowired
    private I18n i18n;

    @Autowired
    public AuthServiceImpl(@Lazy UserAgentService userAgentService) {
        this.userAgentService = userAgentService;
    }
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
    public RefreshTokenResponse login(User user, LoginRequest loginRequest, ApiClient apiClient, String userAgent, IpAddress ipAddress) {
        Optional<UserAgent> findAgent = userAgentService.findByAgent(userAgent);
        UserAgent agent = findAgent.orElseGet(() -> userAgentService.save(new UserAgent(userAgent)));
        LoginLog loginLog = loginLogService.save(new LoginLog(loginRequest.getLoginFrom(), user, ipAddress, loginRequest.getDeviceId(), agent));
        AccessToken token = accessTokenService.generateRefreshToken(user, apiClient, loginLog, loginRequest.getFcmToken());

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

//        User user = userService.findByEmail(refreshTokenRequest.getEmail()).orElseThrow(
//                () -> new ApiException(new ApiError(HttpStatus.UNAUTHORIZED, i18n.getMessage("error.error"),
//                        i18n.getMessage("error.userNotFoundWithEmail", refreshTokenRequest.getEmail()))));

        //update refresh token
        Date dateExpired = jwtService.expireTimeFromNow();
        String token = UUID.randomUUID() + "-" + DateUtil.getCurrentMilliTimeStamp();
        accessToken.setToken(token);
        accessToken.setExpiresAt(dateExpired);
        accessTokenService.save(accessToken);

        return RefreshTokenResponse.builder()
                .authenticationToken(jwtService.toToken(token, apiClient))
                .refreshToken(token)
                .expiresAt(dateExpired)
                .build();
    }

    @Override
    public void verifyAccount(String token) {
        Optional<AccessToken> verificationToken = accessTokenService.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new AppException("Invalid Token")));
    }
}
