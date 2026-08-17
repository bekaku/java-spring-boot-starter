package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.AccessTokenDto;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.mapper.AccessTokenMapper;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.LoginLog;
import com.bekaku.api.spring.mybatis.AccessTokenMybatis;
import com.bekaku.api.spring.mybatis.AppUserMybatis;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.repository.AccessTokenRepository;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.service.UserAgentService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final UserAgentService userAgentService;
    private final AccessTokenMybatis accessTokenMybatis;
    private final AccessTokenMapper mapper;
    private final AppUserMybatis appUserMybatis;
    private final JwtService jwtService;
    private final ApiClientService apiClientService;
    private final AppProperties appProperties;

    @Autowired
    public AccessTokenServiceImpl(@Lazy JwtService jwtService,
                                  @Lazy ApiClientService apiClientService,
                                  AccessTokenRepository accessTokenRepository,
                                  UserAgentService userAgentService,
                                  AccessTokenMybatis accessTokenMybatis,
                                  AccessTokenMapper mapper,
                                  AppUserMybatis appUserMybatis,
                                  AppProperties appProperties) {
        this.jwtService = jwtService;
        this.accessTokenRepository = accessTokenRepository;
        this.userAgentService = userAgentService;
        this.accessTokenMybatis = accessTokenMybatis;
        this.mapper = mapper;
        this.appUserMybatis = appUserMybatis;
        this.apiClientService = apiClientService;
        this.appProperties = appProperties;
    }

    @Override
    public List<String> findAllFcmTokenByUserId(Long userId) {
        return accessTokenRepository.findAllFcmTokenByUserId(userId);
    }

    @Override
    public List<AccessToken> findAllByFcmToken(String fcmToken) {
        return accessTokenRepository.findAllByFcmToken(fcmToken);
    }

    @Override
    
    public Optional<AccessToken> findByToken(String token) {
        return accessTokenRepository.findByToken(HashUtil.sha256(token));
    }

    @Override
    public Optional<AccessToken> findByJwtToken(String jwtToken, String apiClientName) {
        Optional<ApiClient> apiClient = apiClientService.findByApiName(apiClientName);
        if (apiClient.isEmpty()) {
            return Optional.empty();
        }
        Optional<String> sub = jwtService.getSubFromToken(jwtToken, apiClient.get());
        if (sub.isPresent()) {
            return findByToken(sub.get());
        }
        return Optional.empty();
    }

    
    @Override
    public Optional<AccessToken> findAccessTokenByTokenAndUser(AppUser appUser, String token) {
        return accessTokenRepository.findAccessTokenByTokenAndUser(appUser, HashUtil.sha256(token));
    }

    
    @Override
    public Optional<AccessToken> findAccessTokenByToken(String token, boolean revoked) {
        return accessTokenRepository.findAccessTokenByToken(HashUtil.sha256(token), revoked);
    }

    @Override
    public AccessToken generateRefreshToken(AppUser appUser, ApiClient apiClient, LoginLog loginLog, String fcmToken) {
        //find user agent or create new if not found
        Date expires = jwtService.getRefreshTokenExpire();
        AccessToken accessToken = new AccessToken(
                appUser,
                expires,
                false,
                apiClient,
                loginLog,
                DateUtil.getLocalDateTimeNow(),
                fcmToken
        );
        return save(accessToken);
    }

    
    @Override
    public List<AccessTokenDto> findAllByUserAndRevoked(Long userId, boolean revoked) {
        List<AccessToken> list = accessTokenRepository.findAllByUserAndRevoked(userId, AccessTokenServiceType.LOGIN, revoked);
        return list.stream()
                .map(this::setDto)
                .collect(Collectors.toList());
    }

    
    @Override
    public List<AccessTokenDto> findAllByUserAndRevoked(Long userId, boolean revoked, Pageable pageable) {
        List<AccessToken> list = accessTokenRepository.findAllByUserAndRevoked(userId, AccessTokenServiceType.LOGIN, revoked, pageable);
        return list.stream()
                .map(this::setDto)
                .collect(Collectors.toList());
    }

    private AccessTokenDto setDto(AccessToken accessToken) {
        return AccessTokenDto.builder()
                .id(accessToken.getId())
                .hostName(accessToken.getLoginLog().getHostName())
                .agent(accessToken.getLoginLog().getUserAgent() != null ? accessToken.getLoginLog().getUserAgent().getAgent() : null)
                .ipAddredd(accessToken.getLoginLog().getIp())
                .createdDate(accessToken.getCreatedDate())
                .lastestActive(accessToken.getLastestActive())
                .loginFrom(accessToken.getLoginLog().getLoginFrom())
                .activeNow(isActiveNow(accessToken))
                .build();
    }

    private boolean isActiveNow(AccessToken accessToken) {
        return accessToken.getLastestActive() != null &&
                DateUtil.datetimeDiffMinutes(accessToken.getLastestActive(), DateUtil.getLocalDateTimeNow()) <= ConstantData.ONLINE_MINUTES_CLAIM;
    }

    
    @Override
    public void validateRefreshToken(String token) {
        accessTokenRepository.findByToken(HashUtil.sha256(token))
                .orElseThrow(() -> new ApiException(new ApiError(HttpStatus.NOT_FOUND, "Invalid refresh Token", "")));
    }


    @Override
    public void deleteRefreshToken(String token) {
        accessTokenRepository.deleteByToken(HashUtil.sha256(token));
    }

    @Override
    public void revokeTokenByUserId(Long userId) {
        accessTokenRepository.revokeTokenByUserId(userId);
    }

    @Override
    public void updateNullFcmToken(String fcmToken) {
        accessTokenRepository.updateNullFcmToken(fcmToken);
    }

    
    @Override
    public Optional<AccessToken> findByTokenAndRevoked(String token, boolean revoked) {
        return accessTokenRepository.findByTokenAndRevoked(HashUtil.sha256(token), revoked);
    }

    
    @Override
    public Optional<AppUserDto> findByAccessTokenKey(String token) {
        return appUserMybatis.findByAccessTokenKey(HashUtil.sha256(token));
    }

    
    @Override
    public Optional<AccessToken> findByActiveToken(String token) {
        return accessTokenRepository.findByActiveToken(HashUtil.sha256(token));
    }

    @Transactional
    @Override
    public void updateLastestActive(LocalDateTime lastestActive, Long id) {
//        accessTokenRepository.updateLastestActive(lastestActive, id);
        accessTokenMybatis.updateLastestActive(lastestActive, id);
    }

    @Transactional
    @Override
    public AccessToken generateTokenBy(AppUser appUser, Date expiresAt, String token, AccessTokenServiceType service) {
        Optional<AccessToken> accessToken = accessTokenRepository.findLatestAccessTokenByUser(appUser, service);
        AccessToken accessTokenResponse = null;
        if (accessToken.isPresent()) {
            boolean isExpired = isTokenExpired(accessToken.get());
            if (isExpired) {
                delete(accessToken.get());
            } else {
                accessTokenResponse = accessToken.get();
            }
        } else {
            accessTokenResponse = new AccessToken();
            accessTokenResponse.onCreateToken(appUser, expiresAt, token, service);
            save(accessTokenResponse);
        }
        return accessTokenResponse;
    }

    @Override
    public Date getExpireDateBy(AccessTokenServiceType service) {
        Date expire = null;
        switch (service) {
            case FORGOT_PASSWORD -> expire = new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_MINUTE * 15);
        }
        return expire;
    }

    @Transactional
    @Override
    public void logoutProcess(AccessToken token) {
        if (token.getFcmToken() != null) {
            List<AccessToken> allTokenByDevice = findAllByFcmToken(token.getFcmToken());
            if (!allTokenByDevice.isEmpty()) {
                accessTokenRepository.deleteAll(allTokenByDevice);
            }
        } else {
            delete(token);
        }
    }

    @Override
    public boolean isTokenExpired(AccessToken accessToken) {
        LocalDateTime now = DateUtil.getLocalDateTimeNow();
        LocalDateTime expireDatetime = DateUtil.convertDateToLacalDatetime(accessToken.getExpiresAt());
        boolean isBefore = DateUtil.isBefore(expireDatetime, now);
        if (isBefore) {
            return true;
        }
        return false;
    }

    
    @Override
    public ResponseListDto<AccessToken> findAllWithPaging(Pageable pageable) {
        return null;
    }

    
    @Override
    public ResponseListDto<AccessToken> findAllWithSearch(SearchSpecification<AccessToken> specification, Pageable pageable) {
        return null;
    }

    
    @Override
    public ResponseListDto<AccessToken> findAllBy(Specification<AccessToken> specification, Pageable pageable) {
        return null;
    }

    
    @Override
    public Page<AccessToken> findAllPageSpecificationBy(Specification<AccessToken> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<AccessToken> findAllPageSearchSpecificationBy(SearchSpecification<AccessToken> specification, Pageable pageable) {
        return null;
    }

    
    @Override
    public List<AccessToken> findAll() {
        return accessTokenRepository.findAll();
    }

    @Transactional
    @Override
    public AccessToken save(AccessToken accessToken) {
        return accessTokenRepository.save(accessToken);
    }

    @Transactional
    @Override
    public AccessToken update(AccessToken accessToken) {
        return accessTokenRepository.save(accessToken);
    }

    
    @Override
    public Optional<AccessToken> findById(Long id) {
        return accessTokenRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(AccessToken accessToken) {
        accessTokenRepository.delete(accessToken);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        accessTokenRepository.deleteById(id);
    }

    @Override
    public AccessToken convertEntityToDto(AccessToken accessToken) {
        return null;
    }

    @Override
    public AccessToken convertDtoToEntity(AccessToken accessToken) {
        return null;
    }
}
