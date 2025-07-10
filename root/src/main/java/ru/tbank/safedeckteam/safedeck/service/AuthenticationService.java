package ru.tbank.safedeckteam.safedeck.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

public interface AuthenticationService {

    void generateRegisterCode(GenerateRegisterCodeRequestDTO dto);

    void generate2FACode(Generate2FACodeRequestDTO dto);

    RegistrationResponseDTO register(RegistrationClientRequestDTO request, HttpServletRequest httpServletRequest);

    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
}
