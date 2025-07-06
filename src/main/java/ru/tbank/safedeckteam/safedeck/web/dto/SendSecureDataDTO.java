package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSecureDataDTO {

    private String cardName;

    private String cardDescription;

    private List<CredentialPairDTO> credentials;
}
