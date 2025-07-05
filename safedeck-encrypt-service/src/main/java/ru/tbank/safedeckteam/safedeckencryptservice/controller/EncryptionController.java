package ru.tbank.safedeckteam.safedeckencryptservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeckencryptservice.dto.CredentialPairDTO;
import ru.tbank.safedeckteam.safedeckencryptservice.dto.DecryptDTO;
import ru.tbank.safedeckteam.safedeckencryptservice.dto.EncryptDTO;
import ru.tbank.safedeckteam.safedeckencryptservice.service.EncryptionService;

import java.util.List;

@RestController
@RequestMapping("/encryption")
@RequiredArgsConstructor
public class EncryptionController {

    private final EncryptionService encryptionService;

    @PostMapping("/encrypt")
    public Boolean encryptCredentials(@Valid @RequestBody EncryptDTO dto) throws Exception {
        return encryptionService.encryptCredentials(dto.getCardId(), dto.getCredentials());
    }

    @PostMapping("/decrypt")
    public ResponseEntity<List<CredentialPairDTO>> decryptCredentials(@Valid @RequestBody DecryptDTO request) throws Exception {
        return ResponseEntity.ok()
                .body(encryptionService.decryptCredentials(request.getCardId()));
    }
}
