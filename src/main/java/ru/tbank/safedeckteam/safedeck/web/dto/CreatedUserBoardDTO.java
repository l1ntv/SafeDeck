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
public class CreatedUserBoardDTO {

    @NotNull(message = "Board name must be not null.")
    private String boardName;
}
