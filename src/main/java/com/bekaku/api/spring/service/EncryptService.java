package com.bekaku.api.spring.service;

import java.security.GeneralSecurityException;

public interface EncryptService {

    String encrypt(String password);
    boolean check(String checkPassword, String realPassword);
    String encryptData(String data) throws GeneralSecurityException;
    String decryptData(String encryptedData) throws GeneralSecurityException;

}
