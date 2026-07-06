package com.ecommerce.backend_orbyte.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class EncryptionUtil {

    private static final String ALGORITHM    = "AES/GCM/NoPadding";
    private static final int    GCM_IV_LEN   = 12; // 96 bits – recommended for GCM
    private static final int    GCM_TAG_LEN  = 128; // bits

    private final SecretKey secretKey;

    public EncryptionUtil(@Value("${encryption.secret:OrbyteEncryptionKey32BytesPadded!}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 16) {
            throw new IllegalStateException("encryption.secret must be at least 16 characters");
        }
        byte[] key32 = new byte[32];
        System.arraycopy(keyBytes, 0, key32, 0, Math.min(keyBytes.length, 32));
        this.secretKey = new SecretKeySpec(key32, "AES");
    }

    public String encrypt(String plainText) {
        if (plainText == null) return null;
        try {
            byte[] iv = new byte[GCM_IV_LEN];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LEN, iv));

            byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buf = ByteBuffer.allocate(GCM_IV_LEN + cipherBytes.length);
            buf.put(iv);
            buf.put(cipherBytes);

            return Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            log.error("Error encrypting data", e);
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null) return null;
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedData);

            ByteBuffer buf = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LEN];
            buf.get(iv);
            byte[] cipherBytes = new byte[buf.remaining()];
            buf.get(cipherBytes);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LEN, iv));

            return new String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting data", e);
            throw new RuntimeException("Error decrypting data", e);
        }
    }
}
