package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.LoginRequest;
import com.bekaku.api.spring.dto.RefreshTokenRequest;
import com.bekaku.api.spring.dto.RefreshTokenResponse;
import com.bekaku.api.spring.enumtype.JwtType;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.exception.AppException;
import com.bekaku.api.spring.model.*;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.UuidUtils;
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
        return RefreshTokenResponse.builder()
                .userId(user.getId())
                .authenticationToken(jwtService.toToken(user, token.getToken(), apiClient, jwtService.expireJwtTimeFromNow(), JwtType.Authen))
                .refreshToken(jwtService.toToken(user, token.getToken(), apiClient, jwtService.expireRefreshTokenTimeFromNow(), JwtType.Refresh))
                .expiresAt(jwtService.expireJwtTimeFromNow())
                .build();
    }


    @Override
    public RefreshTokenResponse refreshToken(AccessToken accessToken, ApiClient apiClient, String userAgent) {
        //update refresh token
        String token = UuidUtils.generateUUID().toString();
        accessToken.setToken(token);
        accessToken.setExpiresAt(jwtService.expireRefreshTokenTimeFromNow());
        accessTokenService.update(accessToken);
        User user = accessToken.getUser();
        return RefreshTokenResponse.builder()
                .authenticationToken(jwtService.toToken(user, token, apiClient, jwtService.expireJwtTimeFromNow(), JwtType.Authen))
                .refreshToken(jwtService.toToken(user, token, apiClient, jwtService.expireRefreshTokenTimeFromNow(),JwtType.Refresh))
                .expiresAt(jwtService.expireJwtTimeFromNow())
                .userId(user.getId())
                .build();
    }

    @Override
    public void verifyAccount(String token) {
        Optional<AccessToken> verificationToken = accessTokenService.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new AppException("Invalid Token")));
    }
}
