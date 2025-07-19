package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CredentialPairDTO {

    private String field;

    private String password;
}
