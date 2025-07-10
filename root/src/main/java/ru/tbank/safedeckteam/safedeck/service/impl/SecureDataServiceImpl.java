package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Card;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.Role;
import ru.tbank.safedeckteam.safedeck.model.exception.CardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ConflictResourceException;
import ru.tbank.safedeckteam.safedeck.repository.CardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.service.SecureDataService;
import ru.tbank.safedeckteam.safedeck.web.dto.CredentialPairDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.DecryptDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.EncryptDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.SecureDataDTO;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecureDataServiceImpl implements SecureDataService {

    private final ClientRepository clientRepository;

    private final CardRepository cardRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public SecureDataDTO findSecureData(Long cardId, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        Board board = card.getBoard();
        if (board == null)
            throw new ConflictResourceException("Card has no board.");
        if (!board.getOwner().equals(client)) {
            List<Role> clientRoles = client.getRoles();
            List<Role> cardRoles = card.getRoles();
            boolean hasAccess = !Collections.disjoint(clientRoles, cardRoles);
            if (!hasAccess)
                throw new ConflictResourceException("Client has no access to this card.");
        }
        String url = "http://localhost:8081/encryption/decrypt";

        ResponseEntity<List<CredentialPairDTO>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(new DecryptDTO(cardId)),
                new ParameterizedTypeReference<List<CredentialPairDTO>>() {
                }
        );
        List<CredentialPairDTO> body = responseEntity.getBody();
        return new SecureDataDTO(body);
    }

    @Override
    public SecureDataDTO changeSecureData(Long cardId, List<CredentialPairDTO> credentials, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        Board board = card.getBoard();
        if (board == null)
            throw new ConflictResourceException("Card has no board.");
        if (!board.getOwner().equals(client))
            throw new ConflictResourceException("Client does not have the right to edit secure data.");

        String url = "http://localhost:8081/encryption/encrypt";
        EncryptDTO encryptDTO = EncryptDTO.builder()
                .cardId(card.getId())
                .credentials(credentials)
                .build();

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(encryptDTO),
                new ParameterizedTypeReference<Boolean>() {
                }
        );
        return new SecureDataDTO(credentials);
    }
}
