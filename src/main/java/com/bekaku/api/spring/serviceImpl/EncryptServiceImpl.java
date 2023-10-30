package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.service.EncryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("encryptService")
public class EncryptServiceImpl implements EncryptService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String encrypt(String password, String salt) {
//        return AppUtil.genHashPassword(password, salt);
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean check(String checkPassword, String realPassword) {
//        return checkPassword.equals(realPassword);
        return passwordEncoder.matches(checkPassword, realPassword);
    }
}
