package com.project.messanger.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptKeysUtil {

    public static String generatePublicKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Размер ключа
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Конвертируем публичный ключ в строку Base64
            return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate public key", e);
        }
    }
}