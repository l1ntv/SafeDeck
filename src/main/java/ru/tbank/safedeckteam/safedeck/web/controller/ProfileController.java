package ru.tbank.safedeckteam.safedeck.web.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.ProfileService;
import ru.tbank.safedeckteam.safedeck.web.dto.PublicNameResponseDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.UpdatePublicNameRequestDTO;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping()
    public ResponseEntity<PublicNameResponseDTO> getPublicName(Principal principal) {
        return ResponseEntity.ok(profileService.getCurrentUserPublicName(principal.getName()));
    }

    @PutMapping()
    public ResponseEntity<PublicNameResponseDTO> updatePublicName(
            Principal principal,
            @RequestBody UpdatePublicNameRequestDTO requestDto) {
        return ResponseEntity.ok(profileService.updatePublicName(principal.getName(), requestDto));
    }
}
