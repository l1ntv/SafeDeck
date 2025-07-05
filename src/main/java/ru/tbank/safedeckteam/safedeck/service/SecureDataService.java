package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.SecureDataDTO;

public interface SecureDataService {

    SecureDataDTO findSecureData(Long cardId, String email);
}
