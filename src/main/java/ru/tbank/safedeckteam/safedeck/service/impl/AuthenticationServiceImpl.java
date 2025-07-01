package ru.tbank.safedeckteam.safedeck.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.safedeckteam.safedeck.configuration.JwtService;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.IP;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ConflictResourceException;
import ru.tbank.safedeckteam.safedeck.model.exception.WrongDataException;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.IPRepository;
import ru.tbank.safedeckteam.safedeck.service.AuthenticationService;
import ru.tbank.safedeckteam.safedeck.web.dto.AuthenticationRequestDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.AuthenticationResponseDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RegistrationClientRequestDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RegistrationResponseDTO;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientRepository clientRepository;

    private final IPRepository ipRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public RegistrationResponseDTO register(RegistrationClientRequestDTO request,
                                            HttpServletRequest httpServletRequest) {
        request.setIP(httpServletRequest.getRemoteAddr());
        request.setDevice(httpServletRequest.getHeader("User-Agent"));
        request.setCountry(httpServletRequest.getLocale() != null ? httpServletRequest.getLocale().getCountry() : "Unknown");
        request.setProvider((String) httpServletRequest.getSession().getAttribute("user-provider"));

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new ConflictResourceException("User with this email already exists.");
        }

        // Проверить, что IP не существует
        IP ip = IP.builder()
                .ip(request.getIP())
                .build();
        ipRepository.save(ip);

        var client = Client.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .publicName(request.getPublicName())
                .device(request.getDevice())
                .country(request.getCountry())
                .isSubscriber(false)
                .registerIp(ip)
                .provider(request.getProvider())
                .build();
        clientRepository.save(client);

        var jwtToken = jwtService.generateToken(client);
        return RegistrationResponseDTO.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        var client = clientRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ClientNotFoundException("Client with this email not found."));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new WrongDataException("Invalid username or password.");
        }

        var jwtToken = jwtService.generateToken(client);
        return AuthenticationResponseDTO.builder()
                .token(jwtToken)
                .build();
    }
}
