package ru.tbank.safedeckteam.safedeck.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAlert(String message) {
        kafkaTemplate.send("alert-topic", message);
    }

    public void sendPromo(String message) {
        kafkaTemplate.send("promo-topic", message);
    }

    public void send2FACode(String message) {
        kafkaTemplate.send("2fa-topic", message);
    }

    public void sendRegisterCode(String message) {
        kafkaTemplate.send("register-topic", message);
    }
}
