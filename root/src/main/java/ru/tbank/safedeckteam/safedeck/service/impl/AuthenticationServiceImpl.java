package ru.tbank.safedeckteam.safedeck.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.tbank.safedeckteam.safedeck.configuration.JwtService;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.IP;
import ru.tbank.safedeckteam.safedeck.model.SecondFA;
import ru.tbank.safedeckteam.safedeck.model.TrustedUserIP;
import ru.tbank.safedeckteam.safedeck.model.exception.*;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.IPRepository;
import ru.tbank.safedeckteam.safedeck.repository.SecondFARepository;
import ru.tbank.safedeckteam.safedeck.repository.TrustedUsersIPRepository;
import ru.tbank.safedeckteam.safedeck.service.AuthenticationService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientRepository clientRepository;

    private final IPRepository ipRepository;

    private final SecondFARepository secondFARepository;

    private final TrustedUsersIPRepository trustedUsersIPRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void generateRegisterCode(GenerateRegisterCodeRequestDTO dto) {
        String email = dto.getEmail();
        if (clientRepository.existsByEmail(email))
            throw new ConflictResourceException("Email already in use.");
        if (secondFARepository.existsByEmail(email)) {
            SecondFA secondFA = secondFARepository.findByEmail(email);
            secondFARepository.delete(secondFA);
        }
        String code = generateCode();
        SecondFA secondFA = SecondFA.builder()
                .email(email)
                .generatedCode(code)
                .build();

        String url = "http://localhost:8087/mail/send-register-code";

        ResponseEntity<SendEmailResponseDTO> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(new SendRegisterCodeDTO(email, code)),
                new ParameterizedTypeReference<SendEmailResponseDTO>() {
                }
        );
        SendEmailResponseDTO sendEmailResponseDTO = responseEntity.getBody();
        if (!sendEmailResponseDTO.getIsSuccess())
            throw new EmailNotSentException("The email has not been sent.");
        secondFARepository.save(secondFA);
    }

    @Override
    public void generate2FACode(Generate2FACodeRequestDTO dto) {
        String email = dto.getEmail();
        if (!clientRepository.existsByEmail(email))
            throw new ClientNotFoundException("Client not registered yet.");
        if (secondFARepository.existsByEmail(email)) {
            SecondFA secondFA = secondFARepository.findByEmail(email);
            secondFARepository.delete(secondFA);
        }

        String code = generateCode();
        SecondFA secondFA = SecondFA.builder()
                .email(email)
                .generatedCode(code)
                .build();

        String url = "http://localhost:8087/mail/send-2fa-code";

        ResponseEntity<SendEmailResponseDTO> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(new Send2FACodeDTO(email, code)),
                new ParameterizedTypeReference<SendEmailResponseDTO>() {
                }
        );
        SendEmailResponseDTO sendEmailResponseDTO = responseEntity.getBody();
        if (!sendEmailResponseDTO.getIsSuccess())
            throw new EmailNotSentException("The email has not been sent.");
        secondFARepository.save(secondFA);
    }

    @Override
    @Transactional
    public RegistrationResponseDTO register(RegistrationClientRequestDTO request,
                                            HttpServletRequest httpServletRequest) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new ConflictResourceException("User with this email already exists.");
        }

        SecondFA secondFA = secondFARepository.findByEmailAndGeneratedCode(request.getEmail(), request.getGeneratedCode())
                .orElseThrow(() -> new GeneratedCodeNotFoundException("Code not found."));
        secondFARepository.delete(secondFA);


        request.setIP(httpServletRequest.getRemoteAddr());
        request.setDevice(httpServletRequest.getHeader("User-Agent"));
        request.setCountry(httpServletRequest.getLocale() != null ? httpServletRequest.getLocale().getCountry() : "Unknown");
        request.setProvider((String) httpServletRequest.getSession().getAttribute("user-provider"));

        IP ip = ipRepository.findByIp(request.getIP())
                .orElse(IP.builder()
                        .ip(request.getIP())
                        .build());
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

        TrustedUserIP trustedUserIP = TrustedUserIP.builder()
                .ip(ip)
                .user(client)
                .build();
        trustedUsersIPRepository.save(trustedUserIP);


        var jwtToken = jwtService.generateToken(client);
        return RegistrationResponseDTO.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        var client = clientRepository.findOptionalByEmail(request.getEmail())
                .orElseThrow(() -> new ClientNotFoundException("Client with this email not found."));

        SecondFA secondFA = secondFARepository.findByEmailAndGeneratedCode(request.getEmail(), request.getGeneratedCode())
                .orElseThrow(() -> new GeneratedCodeNotFoundException("Code not found."));
        secondFARepository.delete(secondFA);

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

    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }
        return code.toString();
    }
}
