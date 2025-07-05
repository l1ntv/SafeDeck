package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.safedeckteam.safedeck.service.SecureDataService;
import ru.tbank.safedeckteam.safedeck.web.dto.SecureDataDTO;

import java.security.Principal;

@RestController
@RequestMapping("/secure-data")
@RequiredArgsConstructor
public class SecureDataController {

    private final SecureDataService secureDataService;

    @GetMapping("/{cardId}")
    public ResponseEntity<SecureDataDTO> getSecureData(@PathVariable Long cardId, Principal principal) {
        return ResponseEntity
                .ok(secureDataService.findSecureData(cardId, principal.getName()));
    }
}
