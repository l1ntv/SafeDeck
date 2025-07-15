package ru.tbank.safedeckteam.safedeck.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.SecureDataService;
import ru.tbank.safedeckteam.safedeck.web.dto.SecureDataDTO;

import java.security.Principal;

@RestController
@RequestMapping("/secure-data")
@RequiredArgsConstructor
public class SecureDataController {

    private final SecureDataService secureDataService;

    @GetMapping("/{cardId}")
    public ResponseEntity<SecureDataDTO> getSecureData(@PathVariable Long cardId, Principal principal, HttpServletRequest request) {
        return ResponseEntity
                .ok(secureDataService.findSecureData(cardId, principal.getName()));
    }

    @PutMapping("/change/{cardId}")
    public ResponseEntity<SecureDataDTO> changeSecureData(@PathVariable Long cardId,
                                                          @RequestBody SecureDataDTO secureDataDTO,
                                                          Principal principal) {
        return ResponseEntity
                .ok(secureDataService.changeSecureData(cardId, secureDataDTO.getCredentials(), principal.getName()));
    }
}
