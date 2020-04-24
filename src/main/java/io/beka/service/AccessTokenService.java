package io.beka.service;

import io.beka.model.entity.AccessToken;
import io.beka.repository.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;

    //Repository
    public AccessToken save(AccessToken accessToken){
        return accessTokenRepository.save(accessToken);
    }
    public Optional<AccessToken> findById(String id){
        return accessTokenRepository.findById(id);
    }
    public void delete(AccessToken accessToken){
        accessTokenRepository.delete(accessToken);
    }

}
