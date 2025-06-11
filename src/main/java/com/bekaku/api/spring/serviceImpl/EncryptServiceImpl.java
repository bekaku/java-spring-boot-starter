package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.service.EncryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service("encryptService")
public class EncryptServiceImpl implements EncryptService {

    private static final String ALGORITHM = "AES";
    //    private static final byte[] key = "jOwttyTbN/16DI5iIT0FMg==".getBytes(); // Use AES-128 key a 128-bit key
    @Value("${app.encrypt-key}")
    String encryptKey;

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

    public byte[] getEncryptKey() {
        return encryptKey.getBytes();
    }

    @Override
    public String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(getEncryptKey(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    @Override
    public String decrypt(String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(getEncryptKey(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }

}
