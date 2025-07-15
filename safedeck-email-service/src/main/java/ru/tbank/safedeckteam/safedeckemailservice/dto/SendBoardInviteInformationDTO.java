package ru.tbank.safedeckteam.safedeckemailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendBoardInviteInformationDTO {

    private String email;

    private String boardName;

}
