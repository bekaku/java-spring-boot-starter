package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.AccessTokenDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.mapper.AccessTokenMapper;
import com.bekaku.api.spring.model.*;
import com.bekaku.api.spring.repository.AccessTokenRepository;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.service.UserAgentService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private UserAgentService userAgentService;
    @Autowired
    private AccessTokenMapper accessTokenMapper;
    private JwtService jwtService;

    Logger logger = LoggerFactory.getLogger(AccessTokenServiceImpl.class);
    @Value("${app.jwt.session-time}")
    int sessionTime;

    @Value("${app.jwt.secret}")
    String jwtSecret;

    @Value("${app.jwt.session-day}")
    Long sessionDay;

    @Autowired
    public AccessTokenServiceImpl(@Lazy JwtService jwtService) {
        this.jwtService = jwtService;
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
    public Optional<AccessToken> findAccessTokenByToken(String token, boolean revoked) {
        return accessTokenRepository.findAccessTokenByToken(token, revoked);
    }

    @Override
    public AccessToken generateRefreshToken(User user, ApiClient apiClient, String userAgent, LoginLog loginLog, String fcmToken) {
        //find user agent or create new if not found
        Optional<UserAgent> findAgent = userAgentService.findByAgent(userAgent);
        UserAgent agent = findAgent.orElseGet(() -> userAgentService.save(new UserAgent(userAgent)));
//        Date expires = new Date(System.currentTimeMillis() + (sessionTime > 0 ? sessionTime * 1000L : DateUtil.MILLS_IN_YEAR));
        AccessToken accessToken = new AccessToken(
                user,
                agent,
                jwtService.expireTimeFromNow(),
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
        List<AccessToken> list = accessTokenRepository.findAllByUserAndRevoked(userId, revoked);
        return list.stream()
                .map(this::setDto)
                .collect(Collectors.toList());
    }

    private AccessTokenDto setDto(AccessToken accessToken) {
        return AccessTokenDto.builder()
                .id(accessToken.getId())
                .hostName(accessToken.getLoginLog().getHostName())
                .agent(accessToken.getUserAgent().getAgent())
                .ipAddredd(accessToken.getLoginLog().getIp())
                .createdDate(accessToken.getCreatedDate())
                .lastestActive(accessToken.getLastestActive())
                .loginFrom(accessToken.getLoginLog().getLoginFrom())
                .activeNow(accessToken.getLastestActive() != null &&
                        DateUtil.datetimeDiffMinutes(accessToken.getLastestActive(), DateUtil.getLocalDateTimeNow()) <= 5)
                .build();
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

    @Override
    public Optional<AccessToken> findByTokenAndRevoked(String token, boolean revoked) {
        return accessTokenRepository.findByTokenAndRevoked(token, revoked);
    }

    @Override
    public void updateLastestActive(LocalDateTime lastestActive, Long id) {
//        accessTokenRepository.updateLastestActive(lastestActive, id);
        accessTokenMapper.updateLastestActive(lastestActive, id);
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
