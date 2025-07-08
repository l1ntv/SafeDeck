package ru.tbank.safedeckteam.safedeck.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.exception.InvalidDataException;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.service.ProfileService;
import ru.tbank.safedeckteam.safedeck.web.dto.PublicNameResponseDto;
import ru.tbank.safedeckteam.safedeck.web.dto.UpdatePublicNameRequestDto;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ClientRepository clientRepository;

    @Override
    public PublicNameResponseDto getCurrentUserPublicName(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));
        return new PublicNameResponseDto(client.getPublicName());
    }

    @Override
    @Transactional
    public PublicNameResponseDto updatePublicName(String email, UpdatePublicNameRequestDto requestDto) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));
        client.setPublicName(requestDto.getNewPublicName());
        clientRepository.save(client);

        return new PublicNameResponseDto(requestDto.getNewPublicName());
    }
}
