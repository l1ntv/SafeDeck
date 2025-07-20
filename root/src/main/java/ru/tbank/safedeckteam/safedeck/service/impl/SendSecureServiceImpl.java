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
import ru.tbank.safedeckteam.safedeck.model.SendSecure;
import ru.tbank.safedeckteam.safedeck.model.exception.CardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ConflictResourceException;
import ru.tbank.safedeckteam.safedeck.model.exception.SendSecureNotFoundException;
import ru.tbank.safedeckteam.safedeck.repository.CardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.SendSecureRepository;
import ru.tbank.safedeckteam.safedeck.service.SendSecureService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;
import ru.tbank.safedeckteam.safedeck.web.mapper.SendSecureMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendSecureServiceImpl implements SendSecureService {

    private final SendSecureRepository sendSecureRepository;

    private final ClientRepository clientRepository;

    private final CardRepository cardRepository;

    private final SendSecureMapper sendSecureMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public SendSecureDTO createSendSecureLink(Long cardId, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        Board board = card.getBoard();
        if (!client.equals(board.getOwner()))
            throw new ConflictResourceException("Client is not the owner of this board.");
        UUID uuid = UUID.randomUUID();
        SendSecure sendSecure = SendSecure.builder()
                .token(uuid.toString())
                .card(card)
                .build();
        return sendSecureMapper.toDto(sendSecureRepository.save(sendSecure));
    }

    @Override
    public SendSecureDataDTO getSendSecureData(String token) {
        SendSecure sendSecure = sendSecureRepository.findByToken(token)
                .orElseThrow(() -> new SendSecureNotFoundException("Send secure not found by token."));

        LocalDateTime createdAt = sendSecure.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        long hoursBetween = ChronoUnit.HOURS.between(createdAt, now);
        if (hoursBetween > 24)
            throw new ConflictResourceException("Send secure token expired.");

        Card card = sendSecure.getCard();
        String url = "http://safedeck-encrypt-service:8081/encryption/decrypt";

        ResponseEntity<List<CredentialPairDTO>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(new DecryptDTO(card.getId())),
                new ParameterizedTypeReference<List<CredentialPairDTO>>() {
                }
        );
        List<CredentialPairDTO> body = responseEntity.getBody();

        SendSecureDataDTO sendSecureDataDTO = new SendSecureDataDTO(card.getName(), card.getDescription(), body);
        sendSecureRepository.delete(sendSecure);
        return sendSecureDataDTO;
    }
}
