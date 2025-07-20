package ru.tbank.safedeckteam.safedeckemailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendAlertDTO {

    private String emailOwner;

    private String publicNameOwner;

    private String emailSuspect;

    private String publicNameSuspect;

    private String boardName;

    private Long boardId;

    private AuthStatus status;
}
