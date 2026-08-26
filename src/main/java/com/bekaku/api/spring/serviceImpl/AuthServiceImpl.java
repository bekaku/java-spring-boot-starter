package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.LoginRequest;
import com.bekaku.api.spring.dto.RefreshTokenResponse;
import com.bekaku.api.spring.enumtype.JwtType;
import com.bekaku.api.spring.enumtype.LoginLogType;
import com.bekaku.api.spring.exception.AppException;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.LoginLog;
import com.bekaku.api.spring.model.UserAgent;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.AuthService;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.service.LoginLogService;
import com.bekaku.api.spring.service.UserAgentService;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.vo.IpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
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
    private AppProperties appProperties;

    @Autowired
    private I18n i18n;

    @Autowired
    public AuthServiceImpl(@Lazy UserAgentService userAgentService) {
        this.userAgentService = userAgentService;
    }

    @Override
    public AppUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AppUserDto appUserDto) {
            return appUserService.findById(appUserDto.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found - id " + appUserDto.getId()));
        }
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            return appUserService.findByUsername(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + user.getUsername()));
        }
        throw new IllegalStateException("Unsupported principal type: "
                + (principal != null ? principal.getClass().getName() : "null"));
    }

    @Transactional
    @Override
    public void fetchUserAndEnable(AccessToken verificationToken) {
        String username = verificationToken.getAppUser().getUsername();
        AppUser appUser = appUserService.findByUsername(username).orElseThrow(() -> new AppException("User not found with name - " + username));
        appUser.setActive(true);
        appUserService.save(appUser);
    }

    @Transactional
    @Override
    public RefreshTokenResponse login(AppUser appUser, LoginRequest loginRequest, ApiClient apiClient, String userAgent, IpAddress ipAddress) {
        return loingProcess(appUser, apiClient, userAgent, ipAddress, loginRequest.getDeviceId(), loginRequest.getLoginFrom(), loginRequest.getFcmToken());
    }



    private RefreshTokenResponse loingProcess(AppUser appUser, ApiClient apiClient, String userAgent, IpAddress ipAddress, String deviceId, LoginLogType loginFrom, String fcmToken) {
        Optional<UserAgent> findAgent = userAgentService.findByAgent(userAgent);
        UserAgent agent = findAgent.orElseGet(() -> userAgentService.save(new UserAgent(userAgent)));
        LoginLog loginLog = loginLogService.save(new LoginLog(loginFrom, appUser, ipAddress, deviceId, agent));
        AccessToken token = accessTokenService.generateRefreshToken(appUser, apiClient, loginLog, fcmToken);
        Date expired = jwtService.getAccessTokenExpire();
        return RefreshTokenResponse.builder()
                .userId(appUser.getId())
                .authenticationToken(jwtService.toToken(appUser, token.getRawToken(), apiClient, expired, JwtType.Authen))
                .refreshToken(token.getRawToken())
                .expiresAt(expired)
                .build();
    }

    @Transactional
    @Override
    public RefreshTokenResponse login(AppUser appUser, ApiClient apiClient, String userAgent, IpAddress ipAddress) {
        return loingProcess(appUser, apiClient, userAgent, ipAddress, null, null, null);
    }


    @Transactional
    @Override
    public RefreshTokenResponse refreshToken(AccessToken accessToken, ApiClient apiClient, String userAgent) {
        Date expired = jwtService.getRefreshTokenExpire();
        Date accessExpired = jwtService.getAccessTokenExpire();

        AppUser appUser = accessToken.getAppUser();
        // revoke the old row instead of overwriting it, so a stolen/rotated token stays detectable on reuse
        accessToken.setRevoked(true);
        accessTokenService.update(accessToken);

        // login_log is unique per access_token row - clone the session info into a fresh LoginLog
        LoginLog oldLog = accessToken.getLoginLog();
        LoginLog loginLog = loginLogService.save(new LoginLog(
                oldLog != null ? oldLog.getLoginFrom() : null,
                appUser,
                new IpAddress(oldLog != null ? oldLog.getIp() : null, oldLog != null ? oldLog.getHostName() : null),
                oldLog != null ? oldLog.getDeviceId() : null,
                oldLog != null ? oldLog.getUserAgent() : null));

        AccessToken newSession = new AccessToken(appUser, expired, false, apiClient,
                loginLog, DateUtil.getLocalDateTimeNow(), accessToken.getFcmToken());
        accessTokenService.save(newSession);

        return RefreshTokenResponse.builder()
                .authenticationToken(jwtService.toToken(appUser, newSession.getRawToken(), apiClient, accessExpired, JwtType.Authen))
                .refreshToken(newSession.getRawToken())
                .expiresAt(expired)
                .userId(appUser.getId())
                .build();
    }

    @Override
    public void verifyAccount(String token) {
        Optional<AccessToken> verificationToken = accessTokenService.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new AppException("Invalid Token")));
    }
}
