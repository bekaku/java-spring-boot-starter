package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.enumtype.JwtType;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.bekaku.api.spring.util.ConstantData.JWT_TYPE_ATT;

@Slf4j
@Component
public class JwtServiceImpl implements JwtService {
    private final SecretKey signatureAlgorithm;
    //    private final String secret;
//    private final int sessionTime;
//    private final int sessionRefershTime;
    private final String UUID = "uuid";

    private final ApiClientService apiClientService;
    private final AppUserService appUserService;
    private final AccessTokenService accessTokenService;
    private final JwtProperties jwtProperties;


    public JwtServiceImpl(ApiClientService apiClientService,
                          AppUserService appUserService,
                          AccessTokenService accessTokenService,
                          JwtProperties jwtProperties) {
//    @Autowired
//    public JwtServiceImpl(@Value("${app.jwt.secret}") String secret,
//                          @Value("${app.jwt.session-time}") int sessionTime,
//                          @Value("${app.jwt.session-refresh-time}") int sessionRefershTime) {
//        this.secret = secret;
//        this.sessionTime = sessionTime;
//        this.sessionRefershTime = sessionRefershTime;
        this.apiClientService = apiClientService;
        this.appUserService = appUserService;
        this.accessTokenService = accessTokenService;
        this.jwtProperties = jwtProperties;
        signatureAlgorithm = Jwts.SIG.HS512.key().build();
    }

    public SecretKey getKey(ApiClient apiClient) {
//        byte[] keyByte = Decoders.BASE64.decode(apiClient.getApiToken());
        byte[] keyByte = Decoders.BASE64.decode(this.jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyByte);
    }


    @Override
    public String toToken(AppUser appUser, String token, ApiClient apiClient, Date expired, JwtType jwtType) {
        Map<String, String> claims = new HashMap<>();
        claims.put(UUID, appUser.getSalt());
        claims.put(JWT_TYPE_ATT, jwtType.name());
        return toTokenBy(token, apiClient, expired, claims);
    }

    private String toTokenBy(String token, ApiClient apiClient, Date expireTime, Map<String, ?> claims) {
        return Jwts.builder()
                .subject(token)
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
    public Optional<String> getUUIDFromToken(String token, ApiClient apiClient) {
        try {
            Optional<Claims> claims = getClaimsFromToken(token, apiClient);
            return claims.map(value -> value.get(UUID).toString());
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
    public Optional<AppUserDto> jwtVerify(String apiclientName, String authorization, String syncActiveHeader) {
        AtomicReference<Optional<AppUserDto>> dto = new AtomicReference<>(Optional.empty());
//        Optional<ApiClient> apiClient = verifyApiClient(apiclientName);
        if (!AppUtil.isEmpty(apiclientName)) {
//        if (apiClient.isPresent()) {
            Optional<String> authToken = getTokenString(authorization);
            if (authToken.isPresent()) {
//                Optional<String> sub = getSubFromToken(authToken.get(), apiClient.get());
//                Optional<Claims> claims = getClaimsFromToken(authToken.get(), apiClient.get());
                // TODO verify apiClient later
                Optional<Claims> claims = getClaimsFromToken(authToken.get(), null);
                if (claims.isPresent()) {
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        String sub = claims.get().getSubject();
//                        String userUuid = (String) claims.get().get(UUID);
                        String jwtTypeString = (String) claims.get().get(JWT_TYPE_ATT);
                        if (!AppUtil.isEmpty(sub) && !AppUtil.isEmpty(jwtTypeString)) {
                            JwtType jwtType = JwtType.valueOf(jwtTypeString);
                            if (jwtType.equals(JwtType.Authen)) {
                                Optional<AppUserDto> userDto = accessTokenService.findByAccessTokenKey(sub);
                                if (userDto.isPresent()) {

                                    //sync online status if required TODO you can implement with Message Queue eg. RabbitMQ
                                    if (syncActiveHeader != null && syncActiveHeader.equals("1")) {
                                        accessTokenService.updateLastestActive(DateUtil.getLocalDateTimeNow(), userDto.get().getAccessTokenId());
                                    }
                                    userDto.get().setToken(sub);
                                    dto.set(userDto);
                                }
                            }
                            /*
                            UserDto userDto;
                            Optional<AccessToken> accessToken = accessTokenService.findByTokenAndRevoked(sub, false);
                            if (accessToken.isPresent()) {
                                userDto = setUserDto(accessToken.get().getUser());
                                if (userDto != null) {
                                    //sync online status if required TODO you can implement with Message Queue eg. RabbitMQ
                                    if(syncActiveHeader!=null && syncActiveHeader.equals("1")) {
                                        accessTokenService.updateLastestActive(DateUtil.getLocalDateTimeNow(), accessToken.get().getId());
                                    }
                                    userDto.setToken(sub);
                                    userDto.setAccessTokenId(accessToken.get().getId());
                                    dto.set(Optional.of(userDto));
                                }
                            } else if (userUuid != null) {
                                Optional<User> userByUUID = userService.findByUUID(userUuid);
                                if (userByUUID.isPresent()) {
                                    userDto = setUserDto(userByUUID.get());
                                    dto.set(Optional.of(userDto));
                                }
                            }
                            */
                        }
                    }
                }
            }
        }

        return dto.get();
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
            //TODO save this apiClient to cache
            Optional<ApiClient> apiClient = apiClientService.findByApiName(apiName);
            if (apiClient.isPresent()) {
                return apiClient.get().getStatus() || apiClient.get().getByPass() ? apiClient : Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public Date expireRefreshTokenTimeFromNow() {
        return new Date(System.currentTimeMillis() + expireRefreshMillisec());
    }

    @Override
    public Date expireJwtTimeFromNow() {
        return new Date(System.currentTimeMillis() + expireMillisec());
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
    public Long expireMillisec() {
        return this.jwtProperties.getSessionTime() * 1000L;
    }

    @Override
    public Long expireRefreshSecond() {
        return (long) this.jwtProperties.getSessionRefreshTime();
    }

    @Override
    public Long expireRefreshMillisec() {
        return expireRefreshSecond() * 1000L;
    }
}
