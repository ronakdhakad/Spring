package com.mailsense.util;

import lombok.extern.slf4j.Slf4j;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256 CBC encryption for Gmail App Passwords.
 * Key must be exactly 32 characters (injected from application.properties).
 * A fresh random IV is generated on every encrypt call.
 */
@Slf4j
public class AesEncryptionUtil {

    private static final String ALGORITHM      = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int    IV_LENGTH      = 16;

    private final SecretKey secretKey;

    public AesEncryptionUtil(String rawKey) {
        if (rawKey == null || rawKey.length() != 32) {
            throw new IllegalArgumentException(
                "AES key must be exactly 32 characters. Got: " +
                (rawKey == null ? "null" : rawKey.length()));
        }
        this.secretKey = new SecretKeySpec(rawKey.getBytes(), ALGORITHM);
    }

    /**
     * Returns String[2]: [0]=ciphertext base64, [1]=IV base64
     */
    public String[] encrypt(String plaintext) {
        try {
            byte[] ivBytes = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));

            return new String[]{
                Base64.getEncoder().encodeToString(encrypted),
                Base64.getEncoder().encodeToString(ivBytes)
            };
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypts ciphertext using stored IV.
     */
    public String decrypt(String encryptedBase64, String ivBase64) {
        try {
            byte[] ivBytes        = Base64.getDecoder().decode(ivBase64);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
            return new String(cipher.doFinal(encryptedBytes), "UTF-8");
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
