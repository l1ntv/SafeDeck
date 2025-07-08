package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.tbank.safedeckteam.safedeck.model.*;
import ru.tbank.safedeckteam.safedeck.model.enums.AccessLevel;
import ru.tbank.safedeckteam.safedeck.model.exception.BoardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.CardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ConflictResourceException;
import ru.tbank.safedeckteam.safedeck.repository.BoardRepository;
import ru.tbank.safedeckteam.safedeck.repository.CardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.ColorRepository;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;
import ru.tbank.safedeckteam.safedeck.web.mapper.CardMapper;
import ru.tbank.safedeckteam.safedeck.web.mapper.RoleMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final ClientRepository clientRepository;

    private final BoardRepository boardRepository;

    private final ColorRepository colorRepository;

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    private final RoleMapper roleMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public UserCardsDTO findBoardCards(Long boardId, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (client.equals(board.getOwner())) {
            return UserCardsDTO.builder()
                    .accessibleCards(cardMapper.toDtoList(board.getCards()))
                    .accessLevel(AccessLevel.OWNER)
                    .build();
        }

        List<Role> userRolesOnBoard = client.getRoles().stream()
                .filter(role -> role.getBoardId() == board.getId())
                .toList();

        if (userRolesOnBoard.isEmpty()) {
            throw new ConflictResourceException("Client has no roles on this board.");
        }

        List<Card> accessibleCards = new ArrayList<>();
        for (Role role : userRolesOnBoard) {
            accessibleCards.addAll(role.getCards());
        }
        return UserCardsDTO.builder()
                .accessibleCards(cardMapper.toDtoList(accessibleCards))
                .accessLevel(AccessLevel.PARTICIPANT)
                .build();
    }

    @Override
    @Transactional
    public CardDTO create(Long boardId, String email, CreatedCardDTO dto) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));

        Color color = colorRepository.save(Color.builder().rgbCode("DEFAULT").build());

        Card card = Card.builder()
                .name(dto.getCardName())
                .description(dto.getCardDescription())
                .roles(roleMapper.toEntityList(dto.getRoles()))
                .color(color)
                .build();
        card.setBoard(board);
        card = cardRepository.save(card);

        String url = "http://localhost:8081/encryption/encrypt";

        EncryptDTO encryptDTO = EncryptDTO.builder()
                .cardId(card.getId())
                .credentials(dto.getSecureData())
                .build();

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(encryptDTO),
                new ParameterizedTypeReference<Boolean>() {
                }
        );

        return cardMapper.toDto(card);
    }

    @Override
    public void delete(Long boardId, Long cardId, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        if (!board.getId().equals(card.getBoard().getId())) {
            throw new ConflictResourceException("The card is not in this board.");
        }
        if (!board.getOwner().equals(card.getBoard().getOwner())) {
            throw new ConflictResourceException("The client is not the owner of the board.");
        }
        cardRepository.deleteById(cardId);
    }

    @Override
    public CardDTO rename(String email, Long boardId, Long cardId, RenamedCardDTO dto) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().getId().equals(client.getId())) {
            throw new ConflictResourceException("The client is not the owner of the board.");
        }
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        if (!card.getBoard().getId().equals(board.getId())) {
            throw new ConflictResourceException("The card is not in this board.");
        }
        card.setName(dto.getNewCardName());
        return cardMapper.toDto(cardRepository.save(card));
    }

    @Override
    public CardDTO changeDescription(String email, Long boardId, Long cardId, ChangedDescriptionCardDTO dto) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().getId().equals(client.getId())) {
            throw new ConflictResourceException("The client is not the owner of the board.");
        }
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        if (!card.getBoard().getId().equals(board.getId())) {
            throw new ConflictResourceException("The card is not in this board.");
        }
        card.setDescription(dto.getNewCardDescription());
        return cardMapper.toDto(cardRepository.save(card));
    }
}
