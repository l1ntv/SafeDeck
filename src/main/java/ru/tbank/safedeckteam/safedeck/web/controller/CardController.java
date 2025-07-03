package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.web.dto.CardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedCardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RenamedCardDTO;

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

    @PostMapping("/{boardId}")
    public ResponseEntity<CardDTO> create(@PathVariable Long boardId,
                                          Principal principal,
                                          @RequestBody CreatedCardDTO cardDTO) {
        return ResponseEntity.ok()
                .body(cardService.create(boardId, principal.getName(), cardDTO));
    }

    @DeleteMapping("/{boardId}/{cardId}")
    public ResponseEntity<Void> delete(@PathVariable Long boardId,
                                       @PathVariable Long cardId,
                                       Principal principal) {
        cardService.delete(boardId, cardId, principal.getName());
        return ResponseEntity.ok()
                .build();
    }

    @PatchMapping("/{boardId}/{cardId}")
    public ResponseEntity<CardDTO> rename(@RequestBody RenamedCardDTO cardDTO,
                                          @PathVariable Long boardId,
                                          @PathVariable Long cardId,
                                          Principal principal) {
        return ResponseEntity.ok()
                .body(cardService.rename(principal.getName(), boardId, cardId, cardDTO));
    }
}
