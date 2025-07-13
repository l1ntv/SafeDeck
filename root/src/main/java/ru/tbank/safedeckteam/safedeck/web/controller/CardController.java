package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.security.Principal;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/{boardId}")
    public ResponseEntity<UserCardsDTO> getBoardCards(@PathVariable Long boardId,
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

    @PatchMapping("/rename/{boardId}/{cardId}")
    public ResponseEntity<CardDTO> rename(@RequestBody RenamedCardDTO cardDTO,
                                          @PathVariable Long boardId,
                                          @PathVariable Long cardId,
                                          Principal principal) {
        return ResponseEntity.ok()
                .body(cardService.rename(principal.getName(), boardId, cardId, cardDTO));
    }

    @PatchMapping("/change-description/{boardId}/{cardId}")
    public ResponseEntity<CardDTO> changeDescription(@RequestBody ChangedDescriptionCardDTO cardDTO,
                                                     @PathVariable Long boardId,
                                                     @PathVariable Long cardId,
                                                     Principal principal) {
        return ResponseEntity.ok()
                .body(cardService.changeDescription(principal.getName(), boardId, cardId, cardDTO));
    }
}
