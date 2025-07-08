package ru.tbank.safedeckteam.safedeck.web.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.ProfileService;
import ru.tbank.safedeckteam.safedeck.web.dto.PublicNameResponseDto;
import ru.tbank.safedeckteam.safedeck.web.dto.UpdatePublicNameRequestDto;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {


    private final ProfileService profileService;

    @GetMapping("/public-name")
    public ResponseEntity<PublicNameResponseDto> getPublicName(Principal principal) {
        return ResponseEntity.ok(profileService.getCurrentUserPublicName(principal.getName()));
    }

    @PutMapping("/public-name")
    public ResponseEntity<PublicNameResponseDto> updatePublicName(
            Principal principal,
            @RequestBody UpdatePublicNameRequestDto requestDto) {
        return ResponseEntity.ok(profileService.updatePublicName(principal.getName(), requestDto));
    }

}
