package ru.tbank.safedeckteam.safedeck.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.EncryptionService;
import ru.tbank.safedeckteam.safedeck.web.dto.CredentialPair;
import ru.tbank.safedeckteam.safedeck.web.dto.DecryptRequest;

import java.util.List;

@RestController
@RequestMapping("/encryption")
public class EncryptionController {
    private final EncryptionService encryptionService;

    @Autowired
    public EncryptionController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @PostMapping("/credentials/encrypt")
    public String encryptCredentials(@Valid @RequestBody List<CredentialPair> credentials) throws Exception {
        return encryptionService.encryptCredentials(credentials);
    }

    @PostMapping("/credentials/decrypt")
    public List<CredentialPair> decryptCredentials(@Valid @RequestBody DecryptRequest request) throws Exception {
        return encryptionService.decryptCredentials(request.getEncryptedData());
    }

    @PostMapping("/credentials/decrypt/{index}")
    public CredentialPair decryptSingleCredential(
            @PathVariable int index,
            @Valid @RequestBody DecryptRequest request) throws Exception {
        return encryptionService.decryptSingleCredential(request.getEncryptedData(), index);
    }


}