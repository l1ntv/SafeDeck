package ru.tbank.safedeckteam.safedeckemailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendNewPasswordDTO {

    private String email;

    private String publicName;

    private String newPassword;
}
