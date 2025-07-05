package ru.tbank.safedeckteam.safedeckemailservice.dto;

import lombok.Data;

@Data
public class SendRegisterCodeDTO {

    private String email;

    private String generatedCode;
}
