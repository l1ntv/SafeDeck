package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EncryptDTO {

    private Long cardId;

    private List<CredentialPairDTO> credentials;
}
