package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.CredentialPair;

import java.util.List;

public interface EncryptionService {
    String encryptCredentials(List<CredentialPair> credentials) throws Exception;
    List<CredentialPair> decryptCredentials(String encryptedData) throws Exception;
    CredentialPair decryptSingleCredential(String encryptedData, int index) throws Exception;


    String encrypt(String data) throws Exception;
    String decrypt(String encryptedData) throws Exception;
}