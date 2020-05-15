package io.beka.service;

import io.beka.model.entity.ApiClient;
import io.beka.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public interface JwtService {
    String toToken(String token, ApiClient apiClient);

    Optional<String> getSubFromToken(String token, ApiClient apiClient);

    Date expireTimeFromNow();

    Date expireTimeOneDay();

    Date expireTimeOneWeek();

    Date expireTimeOneMonth();

    Date ExpireTimeOneYear();

    int expireMillisec();
}
