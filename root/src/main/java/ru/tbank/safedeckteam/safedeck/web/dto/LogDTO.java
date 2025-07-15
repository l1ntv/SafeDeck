package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.safedeckteam.safedeck.model.IP;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogDTO {
    private Long logId;

    private String email;

    private IP ip;

    private LocalDateTime viewTime;

    private Long cardId;

    private AuthStatus status;
}
