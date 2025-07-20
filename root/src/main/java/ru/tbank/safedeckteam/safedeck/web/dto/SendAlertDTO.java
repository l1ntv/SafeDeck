package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;

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