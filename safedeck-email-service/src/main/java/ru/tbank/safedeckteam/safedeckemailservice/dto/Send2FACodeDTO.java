package ru.tbank.safedeckteam.safedeckemailservice.dto;

import lombok.Data;

@Data
public class Send2FACodeDTO {

    private String email;

    private String generatedCode;
}
