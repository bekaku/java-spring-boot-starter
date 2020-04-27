package io.beka.service;

import io.beka.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public interface JwtService {
    String toToken(String token);

    Optional<String> getSubFromToken(String token);

    Date expireTimeFromNow();

    int expireMillisec();
}
