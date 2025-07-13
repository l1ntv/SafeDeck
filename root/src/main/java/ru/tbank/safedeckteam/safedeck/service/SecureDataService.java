package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.CredentialPairDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.SecureDataDTO;

import java.util.List;

public interface SecureDataService {

    SecureDataDTO findSecureData(Long cardId, String email);

    SecureDataDTO changeSecureData(Long cardId, List<CredentialPairDTO> credentials, String email);
}
