package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Card;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.Role;
import ru.tbank.safedeckteam.safedeck.repository.BoardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.web.dto.CardDTO;
import ru.tbank.safedeckteam.safedeck.web.mapper.CardMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final ClientRepository clientRepository;

    private final BoardRepository boardRepository;

    private final CardMapper cardMapper;

    @Override
    public List<CardDTO> findBoardCards(Long boardId, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));

        // Проверка: владелец ли доски
        if (client.equals(board.getOwner())) {
            return cardMapper.toDtoList(new ArrayList<>(board.getCards()));
        }

        // Получаем роли пользователя на этой доске
        List<Role> userRolesOnBoard = client.getRoles().stream()
                .filter(role -> role.getBoardId() == board.getId())
                .toList();

        if (userRolesOnBoard.isEmpty()) {
            throw new RuntimeException("Client has no roles on this board.");
        }

        // Собираем все доступные карточки через роли
        List<Card> accessibleCards = new ArrayList<>();
        for (Role role : userRolesOnBoard) {
            accessibleCards.addAll(role.getCards());
        }

        return cardMapper.toDtoList(accessibleCards);
    }
}
