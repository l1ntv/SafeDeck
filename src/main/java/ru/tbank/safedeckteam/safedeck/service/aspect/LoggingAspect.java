package ru.tbank.safedeckteam.safedeck.service.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.service.SecureLogService;
import ru.tbank.safedeckteam.safedeck.service.impl.SecureDataServiceImpl;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedLogDTO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final SecureLogService secureLogService;

    @Before("execution(* ru.tbank.safedeckteam.safedeck.service.impl.SecureDataServiceImpl.findSecureData(..))")
    public void logCardShow(JoinPoint joinPoint) {
        Object [] args = joinPoint.getArgs();
        Long cardId = (Long) args[0];
        String email = (String) args[1];
        CreatedLogDTO createdLogDTO = CreatedLogDTO.builder()
                .email(email)
                .viewTime(LocalDateTime.now())
                .cardId(cardId)
                .build();
        secureLogService.createLog(createdLogDTO);
    }
}
