package ru.tbank.safedeckteam.safedeck.service.aspect;

import jakarta.security.auth.message.ClientAuth;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.safedeckteam.safedeck.model.*;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;
import ru.tbank.safedeckteam.safedeck.model.exception.*;
import ru.tbank.safedeckteam.safedeck.repository.CardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.IPRepository;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.service.SecureDataService;
import ru.tbank.safedeckteam.safedeck.service.SecureLogService;
import ru.tbank.safedeckteam.safedeck.service.StatusService;
import ru.tbank.safedeckteam.safedeck.service.impl.SecureDataServiceImpl;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final SecureLogService secureLogService;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final CardRepository cardRepository;

    @Around("execution(* ru.tbank.safedeckteam.safedeck.web.controller.SecureDataController.getSecureData(..))")
    public Object logCardShow(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object [] args = proceedingJoinPoint.getArgs();
        Long cardId = (Long) args[0];
        Principal principal = (Principal) args[1];
        String email = principal.getName();
        HttpServletRequest httpServletRequest = (HttpServletRequest) args[2];

        CreatedLogDTO createdLogDTO = CreatedLogDTO.builder()
                .email(email)
                .httpServletRequest(httpServletRequest)
                .viewTime(LocalDateTime.now())
                .cardId(cardId)
                .build();

        Status status = secureLogService.createLog(createdLogDTO);

        if (Objects.equals(status.getName(), AuthStatus.SUSPECT.name())) {
            throw new ClientSuspectedException("Client has been suspected.");
        }

        if (Objects.equals(status.getName(), AuthStatus.HACK.name())) {
            UUID uuid = UUID.randomUUID();
            Client client = clientRepository.findOptionalByEmail(email)
                    .orElseThrow(() -> new ClientNotFoundException("Client not found."));
            client.setPassword(passwordEncoder.encode(uuid.toString()));
            clientRepository.save(client);
            String url = "http://safedeck-email-service:8087/mail/send-new-password";

            ResponseEntity<SendEmailResponseDTO> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(new SendNewPasswordDTO(email, client.getPublicName(), uuid.toString())),
                    new ParameterizedTypeReference<SendEmailResponseDTO>() {
                    }
            );
            SendEmailResponseDTO sendEmailResponseDTO = responseEntity.getBody();
            if (!sendEmailResponseDTO.getIsSuccess())
                throw new EmailNotSentException("The email has not been sent.");

            url = "http://safedeck-email-service:8087/mail/send-alert";

            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new CardNotFoundException("Card not found."));
            Board board = card.getBoard();
            Client owner = board.getOwner();

            responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(new SendAlertDTO(
                            owner.getEmail(),
                            owner.getPublicName(),
                            email,
                            client.getPublicName(),
                            board.getName(),
                            board.getId(),
                            AuthStatus.HACK)),
                    new ParameterizedTypeReference<SendEmailResponseDTO>() {
                    }
            );
            sendEmailResponseDTO = responseEntity.getBody();
            if (!sendEmailResponseDTO.getIsSuccess())
                throw new EmailNotSentException("The email has not been sent.");

            throw new ClientHackedException("Client has been hacked.");
        }

        return proceedingJoinPoint.proceed();
    }


}
