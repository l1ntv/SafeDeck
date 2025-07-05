package ru.tbank.safedeckteam.safedeckencryptservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class EncryptDTO {

    private Long cardId;

    private List<CredentialPairDTO> credentials;
}
