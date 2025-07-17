package ru.tbank.safedeckteam.safedeck.web.dto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.Status;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedLogDTO {

    @NotNull(message = "Email must be not null")
    private String email;

    @NotNull(message = "Http servlet request must be not null")
    private HttpServletRequest httpServletRequest;

    private Status status;

    private LocalDateTime viewTime;

    @NotNull(message = "CardId must be not null")
    private Long cardId;
}
