package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.PublicNameResponseDto;
import ru.tbank.safedeckteam.safedeck.web.dto.UpdatePublicNameRequestDto;

public interface ProfileService {
    PublicNameResponseDto getCurrentUserPublicName(String email);
    PublicNameResponseDto updatePublicName(String email, UpdatePublicNameRequestDto requestDto);
}
