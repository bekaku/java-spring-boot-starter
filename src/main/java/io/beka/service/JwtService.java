package io.beka.service;

import io.beka.model.entity.Users;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface JwtService {
    String toToken(Users users);

    Optional<String> getSubFromToken(String token);
}
