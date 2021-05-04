package io.beka.serviceImpl;

import io.beka.vo.Paging;
import io.beka.dto.ResponseListDto;
import io.beka.exception.ApiError;
import io.beka.exception.ApiException;
import io.beka.model.*;
import io.beka.repository.AccessTokenRepository;
import io.beka.service.AccessTokenService;
import io.beka.service.UserAgentService;
import io.beka.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final UserAgentService userAgentService;

    @Value("${app.jwt.session-time}")
    int sessionTime;

    @Value("${app.jwt.secret}")
    String jwtSecret;

    @Override
    @Transactional(readOnly = true)
    public Optional<AccessToken> findByToken(String token) {
        return accessTokenRepository.findByToken(token);
    }

    @Override
    public AccessToken generateRefreshToken(User user, ApiClient apiClient, String userAgent, LoginLog loginLog) {
        //find user agent or create new if not found
        Optional<UserAgent> findAgent = userAgentService.findByAgent(userAgent);
        UserAgent agent = findAgent.orElseGet(() -> userAgentService.save(new UserAgent(userAgent)));
        Date expires = new Date(System.currentTimeMillis() + (sessionTime > 0 ? sessionTime * 1000L : DateUtil.MILLS_IN_YEAR));
        AccessToken accessToken = new AccessToken(
                user,
                agent,
                expires,
                false,
                apiClient,
                loginLog
        );
        return save(accessToken);
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
    public ResponseListDto<AccessToken> findAllWithPaging(Pageable pageable) {
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
