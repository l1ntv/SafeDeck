package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.tbank.safedeckteam.safedeck.model.*;
import ru.tbank.safedeckteam.safedeck.model.enums.AccessLevel;
import ru.tbank.safedeckteam.safedeck.model.exception.BoardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.CardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ConflictResourceException;
import ru.tbank.safedeckteam.safedeck.repository.*;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;
import ru.tbank.safedeckteam.safedeck.web.mapper.CardMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final ClientRepository clientRepository;

    private final BoardRepository boardRepository;

    private final ColorRepository colorRepository;

    private final CardRepository cardRepository;

    private final RoleRepository roleRepository;

    private final CardMapper cardMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public UserCardsDTO findBoardCards(Long boardId, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        for (Card card : board.getCards()) {
            Hibernate.initialize(card.getRoles());
        }

        if (client.equals(board.getOwner())) {
            return UserCardsDTO.builder()
                    .accessibleCards(cardMapper.toDtoList(board.getCards()))
                    .accessLevel(AccessLevel.OWNER)
                    .build();
        }
        if (!board.getClients().contains(client))
            throw new ConflictResourceException("The client does not have the right to view this board.");

        List<Role> userRolesOnBoard = roleRepository.findRolesByClientIdAndBoardId(client.getId(), boardId);

        if (userRolesOnBoard.isEmpty())
            throw new ConflictResourceException("Client has no roles on this board.");
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
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().equals(client))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Color color = colorRepository.save(Color.builder().rgbCode("DEFAULT").build());
        Card card = Card.builder()
                .name(dto.getCardName())
                .description(dto.getCardDescription())
                .color(color)
                .board(board)
                .build();
        if (dto.getRoles() != null) {
            List<Role> roles = roleRepository.findAllById(dto.getRoles()
                    .stream()
                    .map(RoleDTO::getRoleId)
                    .toList());
            for (Role role : roles) {
                if (!role.getBoardId().equals(board.getId()))
                    throw new ConflictResourceException("The role does not apply to this board.");
                role.getCards().add(card);
            }
            card.setRoles(roles);
        }

        card = cardRepository.save(card);
        String url = "http://safedeck-encrypt-service:8081/encryption/encrypt";

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
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().equals(client))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        if (!board.getId().equals(card.getBoard().getId()))
            throw new ConflictResourceException("The card is not in this board.");
        card.getRoles().forEach(role -> role.getCards().remove(card));
        cardRepository.deleteById(cardId);
    }

    @Override
    public CardDTO rename(String email, Long boardId, Long cardId, RenamedCardDTO dto) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().getId().equals(client.getId()))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        if (!card.getBoard().getId().equals(board.getId()))
            throw new ConflictResourceException("The card is not in this board.");
        card.setName(dto.getNewCardName());
        return cardMapper.toDto(cardRepository.save(card));
    }

    @Override
    public CardDTO changeDescription(String email, Long boardId, Long cardId, ChangedDescriptionCardDTO dto) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().getId().equals(client.getId()))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found."));
        if (!card.getBoard().getId().equals(board.getId()))
            throw new ConflictResourceException("The card is not in this board.");
        card.setDescription(dto.getNewCardDescription());
        return cardMapper.toDto(cardRepository.save(card));
    }
}
