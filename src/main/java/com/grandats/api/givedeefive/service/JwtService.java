package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.UserDto;
import com.grandats.api.givedeefive.model.ApiClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public interface JwtService {
    String toToken(String token, ApiClient apiClient);

    Optional<String> getSubFromToken(String token, ApiClient apiClient);

    Optional<UserDto> jwtVerify(String apiclientName, String authorization);

    Date expireTimeFromNow();

    Date expireTimeOneDay();

    Date expireTimeOneWeek();

    Date expireTimeOneMonth();

    Date ExpireTimeOneYear();

    int expireMillisec();
}
