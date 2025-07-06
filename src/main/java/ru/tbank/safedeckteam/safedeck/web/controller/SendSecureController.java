package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.SendSecureService;
import ru.tbank.safedeckteam.safedeck.web.dto.SendSecureDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.SendSecureDataDTO;

import java.security.Principal;

@RestController
@RequestMapping("/send-secure")
@RequiredArgsConstructor
public class SendSecureController {

    private final SendSecureService sendSecureService;

    @PostMapping("/{cardId}")
    public ResponseEntity<SendSecureDTO> createSendSecureLink(@PathVariable Long cardId,
                                                              Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sendSecureService.createSendSecureLink(cardId, principal.getName()));
    }

    @GetMapping("/{token}")
    public ResponseEntity<SendSecureDataDTO> getSendSecureData(@PathVariable String token) {
        return ResponseEntity.ok(sendSecureService.getSendSecureData(token));
    }
}
