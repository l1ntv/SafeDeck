package ru.tbank.safedeckteam.safedeck.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedCardDTO {

    @NotNull(message = "Card name must be not null.")
    @Size(max = 50, message = "Card name cannot exceed 50 characters.")
    private String cardName;

    @NotNull(message = "Card description must be not null.")
    @Size(max = 255, message = "Card description cannot exceed 255 characters.")
    private String cardDescription;

    @NotNull(message = "Roles must be not null.")
    private List<RoleDTO> roles;

    @NotNull(message = "Secure data must be not null.")
    private List<CredentialPairDTO> secureData;
}
