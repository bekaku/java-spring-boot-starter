package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.enumtype.JwtType;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppUser;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public interface JwtService {
    String toToken(AppUser appUser, String token, ApiClient apiClient, Date expired, JwtType jwtType);

    Optional<String> getSubFromToken(String token, ApiClient apiClient);
    Optional<String> getSubFromAuthorizationHeader(String authorization, ApiClient apiClient);

    Optional<String> getUUIDFromToken(String token, ApiClient apiClient);

    Optional<JwtType> getJwtTypeFromToken(String token, ApiClient apiClient);

    Optional<String> getExpiredSubFromToken(String token, ApiClient apiClient);

    Optional<AppUserDto> jwtVerify(String apiclientName, String authorization, String syncActiveHeader);

    Optional<String> getAuthorizatoinTokenString(String header);

    Date expireRefreshTokenTimeFromNow();

    Date expireJwtTimeFromNow();

    Date expireTimeOneDay();

    Date expireTimeOneWeek();

    Date expireTimeOneMonth();

    Date ExpireTimeOneYear();

    Long expireMillisec();

    Long expireRefreshSecond();

    Long expireRefreshMillisec();
}
