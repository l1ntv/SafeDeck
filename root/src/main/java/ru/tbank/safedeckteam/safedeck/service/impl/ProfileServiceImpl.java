package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.service.ProfileService;
import ru.tbank.safedeckteam.safedeck.web.dto.PublicNameResponseDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.UpdatePublicNameRequestDTO;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ClientRepository clientRepository;

    @Override
    public PublicNameResponseDTO getCurrentUserPublicName(String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));
        return new PublicNameResponseDTO(client.getPublicName());
    }

    @Override
    @Transactional
    public PublicNameResponseDTO updatePublicName(String email, UpdatePublicNameRequestDTO requestDto) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));
        client.setPublicName(requestDto.getNewPublicName());
        clientRepository.save(client);
        return new PublicNameResponseDTO(requestDto.getNewPublicName());
    }
}
