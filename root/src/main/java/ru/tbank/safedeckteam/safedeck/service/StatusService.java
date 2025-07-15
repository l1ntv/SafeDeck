package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.model.Status;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;
import ru.tbank.safedeckteam.safedeck.web.dto.ClientDataDTO;

public interface StatusService {
    Status determineStatus(ClientDataDTO clientDataDTO);
}
