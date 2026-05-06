 package com.smartemailanalyzer.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple SHA-256 password hashing utility.
 * This avoids introducing a security framework while keeping passwords out of plain text.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String rawValue) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(rawValue.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte hashedByte : hashedBytes) {
                builder.append(String.format("%02x", hashedByte));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    public static boolean matches(String rawValue, String hashedValue) {
        return hash(rawValue).equals(hashedValue);
    }
}
