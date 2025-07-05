package ru.tbank.safedeckteam.safedeckencryptservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialPairDTO {

    private String field;

    private String password;
}
