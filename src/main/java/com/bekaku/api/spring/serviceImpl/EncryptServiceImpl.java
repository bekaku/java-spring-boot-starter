package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.service.EncryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Service("encryptService")
public class EncryptServiceImpl implements EncryptService {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_BYTES = 12;
    //    private static final byte[] key = "jOwttyTbN/16DI5iIT0FMg==".getBytes(); // Use AES-128 key a 128-bit key
    @Value("${app.encrypt-key}")
    String encryptKey;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String encrypt(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean check(String checkPassword, String realPassword) {
        return passwordEncoder.matches(checkPassword, realPassword);
    }

    public byte[] getEncryptKey() {
        return encryptKey.getBytes();
    }

    @Override
    public String encryptData(String data) throws GeneralSecurityException {
        byte[] iv = new byte[IV_BYTES];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey(), new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        ByteBuffer buffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        buffer.put(iv).put(encryptedData);
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    @Override
    public String decryptData(String encryptedData) throws GeneralSecurityException {
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey(),
                new GCMParameterSpec(GCM_TAG_BITS, decodedData, 0, IV_BYTES));
        byte[] decryptedData = cipher.doFinal(decodedData, IV_BYTES, decodedData.length - IV_BYTES);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    private SecretKey secretKey() {
        return new SecretKeySpec(getEncryptKey(), ALGORITHM);
    }
}
