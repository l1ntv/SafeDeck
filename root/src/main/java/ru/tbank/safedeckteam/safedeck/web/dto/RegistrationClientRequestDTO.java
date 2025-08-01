package ru.tbank.safedeckteam.safedeck.web.dto;

import jakarta.validation.constraints.Email;
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
public class RegistrationClientRequestDTO {

    @Email(message = "Email must be valid.")
    @NotNull(message = "Email must be not null.")
    private String email;

    @NotNull(message = "Password must be not null.")
    @Size(min = 6, message = "Password must be greater than 6.")
    private String password;

    @NotNull(message = "Public name must be not null.")
    @Size(max = 50, message = "Public name cannot exceed 50 characters.")
    private String publicName;

    @NotNull(message = "Generated code must be not null.")
    @Size(min = 5, max = 5, message = "Generated code cannot exceed 50 characters.")
    private String generatedCode;

    private String IP;

    private String country;

    private String provider;

    private String device;
}
