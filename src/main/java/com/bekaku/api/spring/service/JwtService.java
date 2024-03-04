package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.model.ApiClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public interface JwtService {
    String toToken(String token, ApiClient apiClient);
    String toToken(String token, ApiClient apiClient, Date expireTime);

    Optional<String> getSubFromToken(String token, ApiClient apiClient);
    Optional<String> getExpiredSubFromToken(String token, ApiClient apiClient);

    Optional<UserDto> jwtVerify(String apiclientName, String authorization);
    Optional<String> getAuthorizatoinTokenString(String header);
    Date expireTimeFromNow();
    Date expireJwtTimeFromNow();

    Date expireTimeOneDay();

    Date expireTimeOneWeek();

    Date expireTimeOneMonth();

    Date ExpireTimeOneYear();

    int expireMillisec();
}
