package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationClientRequestDTO {

    private String email;

    private String password;

    private String publicName;

    private String IP;

    private String country;

    private String provider;

    private String device;
}
