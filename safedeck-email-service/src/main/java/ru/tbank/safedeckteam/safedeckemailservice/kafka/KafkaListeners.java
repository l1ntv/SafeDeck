package ru.tbank.safedeckteam.safedeckemailservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.safedeckteam.safedeckemailservice.dto.Alert;
import ru.tbank.safedeckteam.safedeckemailservice.dto.PromoOffer;
import ru.tbank.safedeckteam.safedeckemailservice.dto.Send2FACodeDTO;
import ru.tbank.safedeckteam.safedeckemailservice.dto.SendRegisterCodeDTO;
import ru.tbank.safedeckteam.safedeckemailservice.service.MailSenderService;

@Component
public class KafkaListeners {
    private final MailSenderService mailSenderService;

    public KafkaListeners(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @KafkaListener(topics = "alert-topic", groupId = "email-service-group")
    public void handAlert(String message) {
        System.out.println("Received Alert: " + message);
        Alert alert;
        try {
            ObjectMapper mapper = new ObjectMapper();
            alert = mapper.readValue(message, Alert.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        mailSenderService.sendAlert(alert);
    }


    @KafkaListener(topics = "promo-topic", groupId = "email-service-group")
    public void handlePromo(String message) {
        System.out.println("Received Promo Offer: " + message);
        PromoOffer promoOffer;
        try {
            ObjectMapper mapper = new ObjectMapper();
            promoOffer = mapper.readValue(message, PromoOffer.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        mailSenderService.sendOffer(promoOffer);
    }

    @KafkaListener(topics = "2fa-topic", groupId = "email-service-group")
    public void handle2FA(String message) {
        System.out.println("Received 2FA Code: " + message);
        Send2FACodeDTO send2FACodeDTO;
        try {
            ObjectMapper mapper = new ObjectMapper();
            send2FACodeDTO = mapper.readValue(message, Send2FACodeDTO.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        mailSenderService.send2FACode(send2FACodeDTO.getEmail(), send2FACodeDTO.getGeneratedCode());
    }

    @KafkaListener(topics = "register-topic", groupId = "email-service-group")
    public void handleRegister(String message) {
        System.out.println("Received Register Code: " + message);
        SendRegisterCodeDTO sendRegisterCodeDTO;
        try {
            ObjectMapper mapper = new ObjectMapper();
            sendRegisterCodeDTO = mapper.readValue(message, SendRegisterCodeDTO.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        mailSenderService.sendRegisterCode(sendRegisterCodeDTO.getEmail(), sendRegisterCodeDTO.getGeneratedCode());
    }
}
