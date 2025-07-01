package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.CardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedCardDTO;

import java.util.List;

public interface CardService {

    List<CardDTO> findBoardCards(Long boardId, String email);

    CardDTO create(Long boardId, String email, CreatedCardDTO card);

    void delete(Long boardId, Long cardId, String email);

    CardDTO rename(String email, Long boardId, CardDTO card);
}
