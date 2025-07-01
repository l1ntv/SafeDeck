package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.CardDTO;

import java.util.List;

public interface CardService {

    List<CardDTO> findBoardCards(Long boardId, String email);

}
