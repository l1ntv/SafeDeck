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
public class ChangedDescriptionCardDTO {

    @NotNull(message = "New card description must be not null.")
    @Size(max = 255, message = "New card description cannot exceed 255 characters.")
    private String newCardDescription;
}
