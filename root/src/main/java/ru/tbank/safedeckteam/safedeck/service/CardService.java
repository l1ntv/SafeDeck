package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.*;

public interface CardService {

    UserCardsDTO findBoardCards(Long boardId, String email);

    CardDTO create(Long boardId, String email, CreatedCardDTO card);

    void delete(Long boardId, Long cardId, String email);

    CardDTO rename(String email, Long boardId, Long cardId, RenamedCardDTO card);

    CardDTO changeDescription(String email, Long boardId, Long cardId, ChangedDescriptionCardDTO card);
}
