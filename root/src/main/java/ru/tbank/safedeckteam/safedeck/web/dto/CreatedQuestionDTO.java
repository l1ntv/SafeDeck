package ru.tbank.safedeckteam.safedeck.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedQuestionDTO {
    @NotNull(message = "Question must be not null.")
    private String question;

    @NotNull(message = "Answer must be not null.")
    private String answer;
}
