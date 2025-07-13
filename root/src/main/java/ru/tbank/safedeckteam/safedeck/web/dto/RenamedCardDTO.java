package ru.tbank.safedeckteam.safedeck.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenamedCardDTO {

    @NotNull(message = "New card name must be not null.")
    @Size(max = 50, message = "New card name cannot exceed 50 characters.")
    private String newCardName;
}
