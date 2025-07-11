package ru.tbank.safedeckteam.safedeck.web.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getPublicName(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(profileService.getCurrentUserPublicName(principal.getName()));
    }

    @PutMapping()
    public ResponseEntity<?> updatePublicName(
            Principal principal,
            @RequestBody UpdatePublicNameRequestDTO requestDto) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (requestDto.getNewPublicName() == null || requestDto.getNewPublicName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Public name cannot be empty");
        }

        return ResponseEntity.ok(profileService.updatePublicName(principal.getName(), requestDto));
    }
}
