package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class JwtServiceImpl implements JwtService {
    private final SecretKey signatureAlgorithm;
    private String secret;
    private final int sessionTime;

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private AccessTokenService accessTokenService;

    Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Autowired
    public JwtServiceImpl(@Value("${app.jwt.secret}") String secret,
                          @Value("${app.jwt.session-time}") int sessionTime) {
        this.secret = secret;
        this.sessionTime = sessionTime;
        signatureAlgorithm = Jwts.SIG.HS512.key().build();
    }

    public SecretKey getKey(ApiClient apiClient) {
//        byte[] keyByte = Decoders.BASE64.decode(apiClient.getApiToken());
        byte[] keyByte = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyByte);
    }

    @Override
    public String toToken(String token, ApiClient apiClient) {
        return toTokenBy(token, apiClient, expireJwtTimeFromNow());
        //test
//        return toTokenBy(token, apiClient, new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_MINUTE * 10));
    }

    private String toTokenBy(String token, ApiClient apiClient, Date expireTime) {
        return Jwts.builder()
                .subject(token)
                .issuedAt(new Date())
//                .claim("hello", "world")
                .expiration(expireTime)
                .signWith(getKey(apiClient))
                .compact();
    }

    @Override
    public String toToken(String token, ApiClient apiClient, Date expireTime) {
        return toTokenBy(token, apiClient, expireTime);
    }

    @Override
    public Optional<String> getSubFromToken(String token, ApiClient apiClient) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(getKey(apiClient)).build().parseSignedClaims(token);
            return Optional.ofNullable(claimsJws.getPayload().getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getExpiredSubFromToken(String token, ApiClient apiClient) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(getKey(apiClient)).build().parseSignedClaims(token);
            return Optional.ofNullable(claimsJws.getPayload().getSubject());
        } catch (ExpiredJwtException e) {
            //don't trust the JWT!
            String subject = e.getClaims().getSubject();
            logger.info("getUnTrustSubFromToken :{}", e.getClaims().getSubject());
            System.out.println("Error: " + subject + "'s jwt failed valiation");
            return Optional.ofNullable(e.getClaims().getSubject());
        }
    }

    @Override
    public Optional<UserDto> jwtVerify(String apiclientName, String authorization) {
        AtomicReference<Optional<UserDto>> dto = new AtomicReference<>(Optional.empty());
        verifyApiClient(apiclientName).flatMap(apiClient ->
                getTokenString(authorization).flatMap(token ->
                        getSubFromToken(token, apiClient))).ifPresent(refreshToken -> {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                accessTokenService.findByTokenAndRevoked(refreshToken, false).ifPresent(accessToken -> {
                    accessTokenService.updateLastestActive(DateUtil.getLocalDateTimeNow(), accessToken.getId());
                    User user = accessToken.getUser();
                    if (user.isActive()) {
                        UserDto userData = new UserDto();
                        userData.setId(user.getId());
                        userData.setToken(refreshToken);
                        userData.setAccessTokenId(accessToken.getId());
                        userData.setUsername(user.getUsername());
                        userData.setEmail(user.getEmail());
                        userData.setActive(user.isActive());
                        dto.set(Optional.of(userData));
                    }
                });
            }
        });
        return dto.get();
    }

    @Override
    public Optional<String> getAuthorizatoinTokenString(String header) {
        return getTokenString(header);
    }

    private Optional<String> getTokenString(String header) {
        if (header == null) {
            return Optional.empty();
        } else {
            String[] split = header.split(" ");
            if (split.length < 2) {
                return Optional.empty();
            } else {
                return Optional.ofNullable(split[1]);
            }
        }
    }

    private Optional<ApiClient> verifyApiClient(String apiName) {
        if (apiName != null) {
            Optional<ApiClient> apiClient = apiClientService.findByApiName(apiName);
            if (apiClient.isPresent()) {
                return apiClient.get().getStatus() || apiClient.get().getByPass() ? apiClient : Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public Date expireTimeFromNow() {
        return new Date(System.currentTimeMillis() + (DateUtil.MILLS_IN_MONTH * 2));//2 months
//        return new Date(System.currentTimeMillis() + (sessionTime > 0 ? sessionTime * 1000L : DateUtil.MILLS_IN_MONTH * 2));//2 months
    }

    @Override
    public Date expireJwtTimeFromNow() {
        return new Date(System.currentTimeMillis() + (DateUtil.MILLS_IN_DAY * 7));
    }

    @Override
    public Date expireTimeOneDay() {
        return new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_DAY);
    }

    @Override
    public Date expireTimeOneWeek() {
        return new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_WEEK);
    }

    @Override
    public Date expireTimeOneMonth() {
        return new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_MONTH);
    }

    @Override
    public Date ExpireTimeOneYear() {
        return new Date(System.currentTimeMillis() + DateUtil.MILLS_IN_YEAR);
    }

    @Override
    public int expireMillisec() {
        return sessionTime * 1000;
    }
}
