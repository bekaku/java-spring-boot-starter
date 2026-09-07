package com.bekaku.api.spring.service;

import com.bekaku.api.spring.model.AppUser;

import java.security.GeneralSecurityException;

public interface EncryptService {

    String encrypt(String password);
    boolean check(String checkPassword, String realPassword);
    String encryptData(String data) throws GeneralSecurityException;
    String decryptData(String encryptedData) throws GeneralSecurityException;

    boolean checkAndMigrate(String rawPassword, AppUser user);
    boolean isOldMd5Format(String password);

}
