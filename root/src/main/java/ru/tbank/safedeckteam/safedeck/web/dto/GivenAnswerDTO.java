package ru.tbank.safedeckteam.safedeck.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GivenAnswerDTO {
    private String correctAnswer;

    @NotNull(message = "Given answer must be not null.")
    private String givenAnswer;
}
