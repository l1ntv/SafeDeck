package ru.tbank.safedeckteam.safedeckemailservice.service;

import ru.tbank.safedeckteam.safedeckemailservice.dto.Alert;
import ru.tbank.safedeckteam.safedeckemailservice.dto.PromoOffer;

public interface MailSenderService {

    Boolean sendRegisterCode(String email, String generatedCode);

    Boolean send2FACode(String email, String generatedCode);

    void sendAlert(Alert alert);

    void sendOffer(PromoOffer promoOffer);
}
