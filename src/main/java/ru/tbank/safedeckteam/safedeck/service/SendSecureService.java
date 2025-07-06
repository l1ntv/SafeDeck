package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.SendSecureDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.SendSecureDataDTO;

public interface SendSecureService {

    SendSecureDTO createSendSecureLink(Long cardId, String email);

    SendSecureDataDTO getSendSecureData(String token);
}
