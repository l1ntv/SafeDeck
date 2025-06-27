package ru.tbank.safedeckteam.safedeck.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.safedeckteam.safedeck.service.AuthenticationService;
import ru.tbank.safedeckteam.safedeck.web.dto.AuthenticationRequestDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.AuthenticationResponseDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RegistrationClientRequestDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RegistrationResponseDTO;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(@RequestBody RegistrationClientRequestDTO request,
                                                            HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authenticationService.register(request, httpServletRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
