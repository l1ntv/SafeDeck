package ru.tbank.safedeckteam.safedeck.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.service.EncryptionService;
import ru.tbank.safedeckteam.safedeck.web.dto.CredentialPair;

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
public class EncryptionServiceImpl implements EncryptionService {

    private final KeyPair rsaKeyPair;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EncryptionServiceImpl() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        rsaKeyPair = keyPairGen.generateKeyPair();
    }

    @Override
    public String encryptCredentials(List<CredentialPair> credentials) throws Exception {
        String jsonData = objectMapper.writeValueAsString(credentials);
        return encrypt(jsonData);
    }

    @Override
    public List<CredentialPair> decryptCredentials(String encryptedData) throws Exception {
        String decryptedJson = decrypt(encryptedData);
        return objectMapper.readValue(decryptedJson, new TypeReference<>() {});
    }

    @Override
    public CredentialPair decryptSingleCredential(String encryptedData, int index) throws Exception {
        List<CredentialPair> credentials = decryptCredentials(encryptedData);
        if (index < 0 || index >= credentials.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        return credentials.get(index);
    }

    @Override
    public String encrypt(String data) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();

        byte[] iv = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, parameterSpec);  // вместо aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
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

    @Override
    public String decrypt(String encryptedData) throws Exception {
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