package ru.tbank.safedeckteam.safedeckencryptservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeckencryptservice.dto.CredentialPairDTO;
import ru.tbank.safedeckteam.safedeckencryptservice.dto.EncryptDTO;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {

    private KeyPair rsaKeyPair;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SecureRepository secureRepository;

    @PostConstruct
    public void init() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        rsaKeyPair = keyPairGen.generateKeyPair();
    }

    @Override
    public void encryptCredentials(Long cardId, List<CredentialPairDTO> credentials) throws Exception {
        String jsonData = objectMapper.writeValueAsString(credentials);
        String encrypt = encrypt(jsonData);
        Secure secure = null;
        if (!secureRepository.existsByCardId(cardId)) {
            secure = Secure.builder()
                    .cardId(cardId)
                    .data(encrypt)
                    .build();
        } else {
            secure = secureRepository.findByCardId(cardId).get();
            secure.setData(encrypt);
        }
        secureRepository.save(secure);
    }

    @Override
    public List<CredentialPairDTO> decryptCredentials(Long cardId) throws Exception {
        Secure secure = secureRepository.findByCardId(cardId).orElse(null);
        String decryptedJson = decrypt(secure.getData());
        return objectMapper.readValue(decryptedJson, new TypeReference<>() {
        });
    }

    private String encrypt(String data) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();

        byte[] iv = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, parameterSpec);
        byte[] encryptedData = aesCipher.doFinal(data.getBytes("UTF-8"));

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaKeyPair.getPublic());
        byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

        byte[] combined = new byte[iv.length + encryptedAesKey.length + encryptedData.length];

        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedAesKey, 0, combined, iv.length, encryptedAesKey.length);
        System.arraycopy(encryptedData, 0, combined, iv.length + encryptedAesKey.length, encryptedData.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    private String decrypt(String encryptedData) throws Exception {
        encryptedData = encryptedData.replaceAll("[^A-Za-z0-9+/=]", "");

        int padding = 0;
        if (encryptedData.length() % 4 != 0) {
            padding = 4 - (encryptedData.length() % 4);
            encryptedData += "=".repeat(padding);
        }

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(encryptedData);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64 data: " + e.getMessage());
        }

        int ivLength = 12;
        int encryptedKeyLength = 256;
        if (decoded.length < ivLength + encryptedKeyLength) {
            throw new IllegalArgumentException("Decoded data too short. Expected at least "
                    + (ivLength + encryptedKeyLength) + " bytes, got " + decoded.length);
        }

        byte[] iv = Arrays.copyOfRange(decoded, 0, ivLength);
        byte[] encryptedAesKey = Arrays.copyOfRange(decoded, ivLength, ivLength + encryptedKeyLength);
        byte[] cipherText = Arrays.copyOfRange(decoded, ivLength + encryptedKeyLength, decoded.length);

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, spec);

        return new String(aesCipher.doFinal(cipherText), StandardCharsets.UTF_8);
    }
}
