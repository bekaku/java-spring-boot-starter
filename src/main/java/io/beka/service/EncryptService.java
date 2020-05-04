package io.beka.service;

public interface EncryptService {

    String encrypt(String password, String salt);
    boolean check(String checkPassword, String realPassword);

}
