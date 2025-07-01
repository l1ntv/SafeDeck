package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.web.dto.CardDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/{boardId}")
    public ResponseEntity<List<CardDTO>> getBoardCards(@PathVariable Long boardId,
                                                      Principal principal) {
        return ResponseEntity.ok()
                .body(cardService.findBoardCards(boardId, principal.getName()));
    }

}
