package ru.tbank.safedeckteam.safedeckencryptservice.service;

import ru.tbank.safedeckteam.safedeckencryptservice.dto.CredentialPairDTO;

import java.util.List;

public interface EncryptionService {

    Boolean encryptCredentials(Long cardId, List<CredentialPairDTO> credentials) throws Exception;

    List<CredentialPairDTO> decryptCredentials(Long cardId) throws Exception;
}
