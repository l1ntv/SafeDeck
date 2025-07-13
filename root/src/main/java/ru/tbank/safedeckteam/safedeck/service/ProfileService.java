package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.PublicNameResponseDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.UpdatePublicNameRequestDTO;

public interface ProfileService {

    PublicNameResponseDTO getCurrentUserPublicName(String email);

    PublicNameResponseDTO updatePublicName(String email, UpdatePublicNameRequestDTO requestDto);
}
