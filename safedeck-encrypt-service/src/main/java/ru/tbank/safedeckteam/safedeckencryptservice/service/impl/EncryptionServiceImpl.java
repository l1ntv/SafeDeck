package ru.tbank.safedeckteam.safedeckencryptservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeckencryptservice.dto.CredentialPairDTO;
import ru.tbank.safedeckteam.safedeckencryptservice.model.Secure;
import ru.tbank.safedeckteam.safedeckencryptservice.repository.SecureRepository;
import ru.tbank.safedeckteam.safedeckencryptservice.service.EncryptionService;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.*;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {

    // Поле для хранения RSA-ключа
    private KeyPair rsaKeyPair;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecureRepository secureRepository;
    private static final int IV_LENGTH = 12;        // 12 bytes for GCM IV
    private static final int AES_KEY_LENGTH = 256;  // 256 bytes = 2048 bits RSA-encrypted AES key

    @PostConstruct
    public void init() {
        try {
            generateRSAKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при инициализации RSA-ключа", e);
        }
    }

    // Генерация RSA-ключа один раз
    private void generateRSAKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        rsaKeyPair = kpg.generateKeyPair();
    }

    @Override
    public void encryptCredentials(Long cardId, List<CredentialPairDTO> credentials) throws Exception {
        String jsonData = objectMapper.writeValueAsString(credentials);
        String encryptedData = encrypt(jsonData);

        Secure secure = secureRepository.findByCardId(cardId).orElse(null);

        if (secure == null) {
            secure = Secure.builder()
                    .cardId(cardId)
                    .data(encryptedData)
                    .build();
        } else {
            secure.setData(encryptedData);
        }

        secureRepository.save(secure);
    }

    @Override
    public List<CredentialPairDTO> decryptCredentials(Long cardId) throws Exception {
        Secure secure = secureRepository.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException("Данные не найдены для cardId: " + cardId));

        String decryptedJson = decrypt(secure.getData());
        return objectMapper.readValue(decryptedJson, new TypeReference<>() {});
    }

    private String encrypt(String data) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        SecretKey aesKey = kg.generateKey();

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] cipherText = aesCipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaKeyPair.getPublic());
        byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

        byte[] combined = new byte[iv.length + encryptedAesKey.length + cipherText.length];

        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedAesKey, 0, combined, iv.length, encryptedAesKey.length);
        System.arraycopy(cipherText, 0, combined, iv.length + encryptedAesKey.length, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    private String decrypt(String encryptedData) throws Exception {
        if (encryptedData == null || encryptedData.isEmpty()) {
            throw new IllegalArgumentException("Зашифрованные данные пусты");
        }

        // Очистка строки от непечатаемых символов
        encryptedData = encryptedData.replaceAll("[^A-Za-z0-9+/=]", "");

        // Добавление padding
        int padding = (4 - (encryptedData.length() % 4)) % 4;
        encryptedData += "=".repeat(padding);

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(encryptedData);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверные Base64 данные: " + e.getMessage());
        }

        if (decoded.length < IV_LENGTH + AES_KEY_LENGTH) {
            throw new IllegalArgumentException("Недостаточно данных для извлечения IV и ключа");
        }

        final int IV_LENGTH = 12;
        final int AES_KEY_LENGTH = 256;

        byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
        byte[] encryptedAesKey = Arrays.copyOfRange(decoded, IV_LENGTH, IV_LENGTH + AES_KEY_LENGTH);
        byte[] cipherText = Arrays.copyOfRange(decoded, IV_LENGTH + AES_KEY_LENGTH, decoded.length);

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);

        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, spec);

        byte[] decrypted = aesCipher.doFinal(cipherText);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}