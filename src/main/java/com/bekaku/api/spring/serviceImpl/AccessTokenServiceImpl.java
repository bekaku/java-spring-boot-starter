package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AccessTokenDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.mapper.AccessTokenMapper;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.LoginLog;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.mybatis.AccessTokenMybatis;
import com.bekaku.api.spring.mybatis.UserMybatis;
import com.bekaku.api.spring.repository.AccessTokenRepository;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.service.UserAgentService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Transactional
@RequiredArgsConstructor
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final UserAgentService userAgentService;
    private final AccessTokenMybatis accessTokenMybatis;
    private final AccessTokenMapper mapper;
    private final UserMybatis userMybatis;
    private final JwtService jwtService;
    private final ApiClientService apiClientService;

    @Value("${app.jwt.session-time}")
    int sessionTime;

    @Value("${app.jwt.secret}")
    String jwtSecret;

    @Value("${app.jwt.session-day}")
    Long sessionDay;

    private final I18n i18n;

    @Autowired
    public AccessTokenServiceImpl(@Lazy JwtService jwtService,
                                  @Lazy ApiClientService apiClientService,
                                  AccessTokenRepository accessTokenRepository,
                                  UserAgentService userAgentService,
                                  AccessTokenMybatis accessTokenMybatis,
                                  AccessTokenMapper mapper,
                                  UserMybatis userMybatis,
                                  I18n i18n) {
        this.jwtService = jwtService;
        this.accessTokenRepository = accessTokenRepository;
        this.userAgentService = userAgentService;
        this.accessTokenMybatis = accessTokenMybatis;
        this.mapper = mapper;
        this.userMybatis = userMybatis;
        this.i18n = i18n;
        this.apiClientService = apiClientService;
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
    @Transactional(readOnly = true)
    public Optional<AccessToken> findByToken(String token) {
        return accessTokenRepository.findByToken(token);
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

    @Transactional(readOnly = true)
    @Override
    public Optional<AccessToken> findAccessTokenByTokenAndUser(User user, String token) {
        return accessTokenRepository.findAccessTokenByTokenAndUser(user, token);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AccessToken> findAccessTokenByToken(String token, boolean revoked) {
        return accessTokenRepository.findAccessTokenByToken(token, revoked);
    }

    @Override
    public AccessToken generateRefreshToken(User user, ApiClient apiClient, LoginLog loginLog, String fcmToken) {
        //find user agent or create new if not found
//        Date expires = new Date(System.currentTimeMillis() + (sessionTime > 0 ? sessionTime * 1000L : DateUtil.MILLS_IN_YEAR));
        AccessToken accessToken = new AccessToken(
                user,
                jwtService.expireRefreshTokenTimeFromNow(),
                false,
                apiClient,
                loginLog,
                DateUtil.getLocalDateTimeNow(),
                fcmToken
        );
        return save(accessToken);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccessTokenDto> findAllByUserAndRevoked(Long userId, boolean revoked) {
        List<AccessToken> list = accessTokenRepository.findAllByUserAndRevoked(userId, AccessTokenServiceType.LOGIN, revoked);
        return list.stream()
                .map(this::setDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public void validateRefreshToken(String token) {
        accessTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiException(new ApiError(HttpStatus.NOT_FOUND, "Invalid refresh Token", "")));
    }


    @Override
    public void deleteRefreshToken(String token) {
        accessTokenRepository.deleteByToken(token);
    }

    @Override
    public void revokeTokenByUserId(Long userId) {
        accessTokenRepository.revokeTokenByUserId(userId);
    }

    @Override
    public void updateNullFcmToken(String fcmToken) {
        accessTokenRepository.updateNullFcmToken(fcmToken);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AccessToken> findByTokenAndRevoked(String token, boolean revoked) {
        return accessTokenRepository.findByTokenAndRevoked(token, revoked);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserDto> findByAccessTokenKey(String token) {
        return userMybatis.findByAccessTokenKey(token);
    }

    @Override
    public void updateLastestActive(LocalDateTime lastestActive, Long id) {
//        accessTokenRepository.updateLastestActive(lastestActive, id);
        accessTokenMybatis.updateLastestActive(lastestActive, id);
    }

    @Override
    public AccessToken generateTokenBy(User user, Date expiresAt, String token, AccessTokenServiceType service) {
        Optional<AccessToken> accessToken = accessTokenRepository.findLatestAccessTokenByUser(user, service);
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
            accessTokenResponse.onCreateToken(user, expiresAt, token, service);
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public List<AccessToken> findAll() {
        return accessTokenRepository.findAll();
    }

    @Override
    public AccessToken save(AccessToken accessToken) {
        return accessTokenRepository.save(accessToken);
    }

    @Override
    public AccessToken update(AccessToken accessToken) {
        return accessTokenRepository.save(accessToken);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AccessToken> findById(Long id) {
        return accessTokenRepository.findById(id);
    }

    @Override
    public void delete(AccessToken accessToken) {
        accessTokenRepository.delete(accessToken);
    }

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
