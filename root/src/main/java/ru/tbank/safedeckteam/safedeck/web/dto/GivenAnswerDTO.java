package ru.tbank.safedeckteam.safedeck.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GivenAnswerDTO {
    private String correctAnswer; // Не заполнять с фронта

    @NotNull(message = "Given answer must be not null.")
    private String givenAnswer;

    private long boardId;

    private long cardId;
}
