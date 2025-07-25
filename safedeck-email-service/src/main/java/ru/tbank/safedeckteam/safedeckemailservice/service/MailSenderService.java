package ru.tbank.safedeckteam.safedeckemailservice.service;

import ru.tbank.safedeckteam.safedeckemailservice.dto.Alert;
import ru.tbank.safedeckteam.safedeckemailservice.dto.AuthStatus;
import ru.tbank.safedeckteam.safedeckemailservice.dto.PromoOffer;

public interface MailSenderService {

    Boolean sendRegisterCode(String email, String generatedCode);

    Boolean send2FACode(String email, String generatedCode);

    Boolean sendBoardInviteInformation(String email, String boardName, String boardId);

    Boolean sendNewPassword(String email, String publicName, String newPassword);

    Boolean sendAlert(String emailOwner, String publicNameOwner, String emailSuspect, String publicNameSuspect, String boardName, Long boardId, AuthStatus status);

    void sendAlert(Alert alert);

    void sendOffer(PromoOffer promoOffer);
}
