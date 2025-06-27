package ru.tbank.safedeckteam.safedeck.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.tbank.safedeckteam.safedeck.web.dto.AuthenticationRequestDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.AuthenticationResponseDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RegistrationClientRequestDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RegistrationResponseDTO;

public interface AuthenticationService {

    RegistrationResponseDTO register(RegistrationClientRequestDTO request, HttpServletRequest httpServletRequest);

    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
}
