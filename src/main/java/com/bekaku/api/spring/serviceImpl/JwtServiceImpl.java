package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.enumtype.JwtType;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.HashUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.bekaku.api.spring.util.ConstantData.JWT_TYPE_ATT;

@Slf4j
@Component
public class JwtServiceImpl implements JwtService {
    private final String UID = "uid";

    private final ApiClientService apiClientService;
    private final AppUserService appUserService;
    private final AccessTokenService accessTokenService;
    private final AppProperties appProperties;


    public JwtServiceImpl(ApiClientService apiClientService,
                          AppUserService appUserService,
                          AccessTokenService accessTokenService,
                          AppProperties appProperties) {
        this.apiClientService = apiClientService;
        this.appUserService = appUserService;
        this.accessTokenService = accessTokenService;
        this.appProperties = appProperties;
    }

    public SecretKey getKey(ApiClient apiClient) {
//        byte[] keyByte = Decoders.BASE64.decode(apiClient.getApiToken());
        byte[] keyByte = Decoders.BASE64.decode(this.appProperties.jwt().secret());
        return Keys.hmacShaKeyFor(keyByte);
    }


    @Override
    public String toToken(AppUser appUser, String token, ApiClient apiClient, Date expired, JwtType jwtType) {
        Map<String, String> claims = new HashMap<>();
        claims.put(UID, appUser.getId().toString());
        claims.put(JWT_TYPE_ATT, jwtType.name());
        return toTokenBy(token, apiClient, expired, claims);
    }

    private String toTokenBy(String sub, ApiClient apiClient, Date expireTime, Map<String, ?> claims) {
        return Jwts.builder()
                .subject(sub)
                .issuedAt(new Date())
                .claims(claims)
                .expiration(expireTime)
                .signWith(getKey(apiClient))
                .compact();
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
    public Optional<String> getSubFromAuthorizationHeader(String authorization, ApiClient apiClient) {
        Optional<String> authToken = getTokenString(authorization);
        if (authToken.isEmpty()) {
            return Optional.empty();
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(getKey(apiClient)).build().parseSignedClaims(authToken.get());
            return Optional.ofNullable(claimsJws.getPayload().getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getUIDFromToken(String token, ApiClient apiClient) {
        try {
            Optional<Claims> claims = getClaimsFromToken(token, apiClient);
            return claims.map(value -> value.get(UID).toString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<JwtType> getJwtTypeFromToken(String token, ApiClient apiClient) {
        try {
            Optional<Claims> claims = getClaimsFromToken(token, apiClient);
            return claims.map(value -> JwtType.valueOf(value.get(JWT_TYPE_ATT).toString()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Claims> getClaimsFromToken(String token, ApiClient apiClient) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(getKey(apiClient)).build().parseSignedClaims(token);
            return Optional.ofNullable(claimsJws.getPayload());
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
            log.info("getUnTrustSubFromToken :{}", e.getClaims().getSubject());
            System.out.println("Error: " + subject + "'s jwt failed valiation");
            return Optional.ofNullable(e.getClaims().getSubject());
        }
    }

    @Override
    public Optional<AppUserDto> jwtVerify(String apiclientName, String jwtToken, String syncActiveHeader) {
        if (AppUtil.isEmpty(apiclientName) || AppUtil.isEmpty(jwtToken)) {
            return Optional.empty();
        }

        //TODO verify apiClient later
//        Optional<ApiClient> apiClient = verifyApiClient(apiclientName);
//        if (apiClient.isEmpty()) {
//            return Optional.empty();
//        }

        Optional<Claims> claims = getClaimsFromToken(jwtToken, null);
        if (claims.isEmpty()) {
            return Optional.empty();
        }
        Claims payload = claims.get();
        String sub = payload.getSubject();
        String jwtTypeString = payload.get(JWT_TYPE_ATT, String.class);
        String userID = payload.get(UID, String.class);
        if (AppUtil.isEmpty(sub) || AppUtil.isEmpty(jwtTypeString) || AppUtil.isEmpty(userID)) {
            return Optional.empty();
        }

        JwtType jwtType;
        try {
            jwtType = JwtType.valueOf(jwtTypeString);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown jwt type claim: {}", jwtTypeString);
            return Optional.empty();
        }
        if (!JwtType.Authen.equals(jwtType)) {
            return Optional.empty();
        }

        long userId;
        try {
            userId = Long.parseLong(userID);
        } catch (NumberFormatException e) {
            log.warn("Invalid uid claim in token");
            return Optional.empty();
        }

        // revocation check: the subject is the session token key - it must exist and not be revoked
        Optional<AccessToken> session = accessTokenService.findByTokenAndRevoked(sub, false);
        if (session.isEmpty()) {
            log.info("Rejected revoked or unknown session token");
            return Optional.empty();
        }
        if ("1".equals(syncActiveHeader)) {
            accessTokenService.updateLastestActive(DateUtil.getLocalDateTimeNow(), session.get().getId());
        }

        AppUserDto userDto = new AppUserDto();
        userDto.setId(userId);
        userDto.setToken(sub);
        userDto.setAccessTokenId(session.get().getId());
        return Optional.of(userDto);
    }

    private AppUserDto setUserDto(AppUser appUser) {
        if (appUser != null && appUser.isActive()) {
            AppUserDto userData = new AppUserDto();
            userData.setId(appUser.getId());
            userData.setUsername(appUser.getUsername());
            userData.setEmail(appUser.getEmail());
            userData.setActive(appUser.isActive());
            return userData;
        }
        return null;
    }

    @Deprecated
    public Optional<AppUserDto> jwtVerifyBy(String apiclientName, String authorization) {
        AtomicReference<Optional<AppUserDto>> dto = new AtomicReference<>(Optional.empty());
        verifyApiClient(apiclientName).flatMap(apiClient ->
                getTokenString(authorization).flatMap(token ->
                        getSubFromToken(token, apiClient))).ifPresent(refreshToken -> {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                accessTokenService.findByTokenAndRevoked(refreshToken, false).ifPresent(accessToken -> {
                    accessTokenService.updateLastestActive(DateUtil.getLocalDateTimeNow(), accessToken.getId());
                    AppUser appUser = accessToken.getAppUser();
                    if (appUser.isActive()) {
                        AppUserDto userData = new AppUserDto();
                        userData.setId(appUser.getId());
                        userData.setToken(refreshToken);
                        userData.setAccessTokenId(accessToken.getId());
                        userData.setUsername(appUser.getUsername());
                        userData.setEmail(appUser.getEmail());
                        userData.setActive(appUser.isActive());
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

    public Optional<String> getTokenString(String header) {
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
            //TODO save this apiClient to cache
            Optional<ApiClient> apiClient = apiClientService.findByApiName(apiName);
            if (apiClient.isPresent()) {
                ApiClient client = apiClient.get();
                boolean isActive = Boolean.TRUE.equals(client.getStatus())
                        || Boolean.TRUE.equals(client.getByPass());
                return isActive ? apiClient : Optional.empty();
            }
        }
        return Optional.empty();
    }

    public Date getAccessTokenExpire() {
        Instant now = Instant.now();
        return Date.from(now.plus(appProperties.jwt().accessTokenTtlMinutes(), ChronoUnit.MINUTES));
    }

    public Date getRefreshTokenExpire() {
        Instant now = Instant.now();
        return Date.from(now.plus(appProperties.jwt().refreshTokenTtlDays(), ChronoUnit.DAYS));
    }

}
