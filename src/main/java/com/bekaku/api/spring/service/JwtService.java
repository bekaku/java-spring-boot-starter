package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.enumtype.JwtType;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public interface JwtService {
    String toToken(String token, ApiClient apiClient);
    String toToken(User user, String token, ApiClient apiClient);
    String toToken(User user, String token, ApiClient apiClient, Date expired);
    String toToken(User user, String token, ApiClient apiClient, Date expired, JwtType jwtType);
    String toToken(String token, ApiClient apiClient, Date expireTime);

    Optional<String> getSubFromToken(String token, ApiClient apiClient);
    Optional<String> getUUIDFromToken(String token, ApiClient apiClient);
    Optional<JwtType> getJwtTypeFromToken(String token, ApiClient apiClient);
    Optional<String> getExpiredSubFromToken(String token, ApiClient apiClient);

    Optional<UserDto> jwtVerify(String apiclientName, String authorization, String syncActiveHeader);
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
