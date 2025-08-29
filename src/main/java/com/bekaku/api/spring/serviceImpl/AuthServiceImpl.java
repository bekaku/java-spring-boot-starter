package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.LoginRequest;
import com.bekaku.api.spring.dto.RefreshTokenResponse;
import com.bekaku.api.spring.enumtype.JwtType;
import com.bekaku.api.spring.exception.AppException;
import com.bekaku.api.spring.model.*;
import com.bekaku.api.spring.service.*;
import com.bekaku.api.spring.util.UuidUtils;
import com.bekaku.api.spring.vo.IpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AppUserService appUserService;
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
    public AppUser getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return appUserService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    @Override
    public void fetchUserAndEnable(AccessToken verificationToken) {
        String username = verificationToken.getAppUser().getUsername();
        AppUser appUser = appUserService.findByUsername(username).orElseThrow(() -> new AppException("User not found with name - " + username));
        appUser.setActive(true);
        appUserService.save(appUser);
    }

    @Override
    public RefreshTokenResponse login(AppUser appUser, LoginRequest loginRequest, ApiClient apiClient, String userAgent, IpAddress ipAddress) {
        Optional<UserAgent> findAgent = userAgentService.findByAgent(userAgent);
        UserAgent agent = findAgent.orElseGet(() -> userAgentService.save(new UserAgent(userAgent)));
        LoginLog loginLog = loginLogService.save(new LoginLog(loginRequest.getLoginFrom(), appUser, ipAddress, loginRequest.getDeviceId(), agent));
        AccessToken token = accessTokenService.generateRefreshToken(appUser, apiClient, loginLog, loginRequest.getFcmToken());
        return RefreshTokenResponse.builder()
                .userId(appUser.getId())
                .authenticationToken(jwtService.toToken(appUser, token.getToken(), apiClient, jwtService.expireJwtTimeFromNow(), JwtType.Authen))
                .refreshToken(jwtService.toToken(appUser, token.getToken(), apiClient, jwtService.expireRefreshTokenTimeFromNow(), JwtType.Refresh))
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
        AppUser appUser = accessToken.getAppUser();
        return RefreshTokenResponse.builder()
                .authenticationToken(jwtService.toToken(appUser, token, apiClient, jwtService.expireJwtTimeFromNow(), JwtType.Authen))
                .refreshToken(jwtService.toToken(appUser, token, apiClient, jwtService.expireRefreshTokenTimeFromNow(),JwtType.Refresh))
                .expiresAt(jwtService.expireJwtTimeFromNow())
                .userId(appUser.getId())
                .build();
    }

    @Override
    public void verifyAccount(String token) {
        Optional<AccessToken> verificationToken = accessTokenService.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new AppException("Invalid Token")));
    }
}
