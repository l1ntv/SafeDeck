package ru.tbank.safedeckteam.safedeckemailservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeckemailservice.dto.Alert;
import ru.tbank.safedeckteam.safedeckemailservice.dto.PromoOffer;
import ru.tbank.safedeckteam.safedeckemailservice.service.MailSenderService;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Override
    public Boolean sendRegisterCode(String email, String generatedCode) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(email);
        mailMessage.setSubject("Подтвердите регистрацию учётной записи SafeDeck.");
        StringBuilder text = new StringBuilder();
        text.append("Пожалуйста, подтвердите ваш email ")
                .append(email)
                .append(".\n")
                .append("Ваш код подтверждения: ")
                .append(generatedCode)
                .append("\nЕсли вы не регистрировались на сайте SafeDeck, просто проигнорируйте данное письмо.");
        mailMessage.setText(text.toString());
        try {
            javaMailSender.send(mailMessage);
        } catch (MailException exception) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean send2FACode(String email, String generatedCode) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(email);
        mailMessage.setSubject("Код двухфакторной аутентификации для SafeDeck.");

        StringBuilder text = new StringBuilder();
        text.append("Пожалуйста, подтвердите ваш вход в учётную запись SafeDeck.\n")
                .append("Для подтверждения входа в ваш аккаунт SafeDeck введите следующий код: ")
                .append(generatedCode)
                .append("\nЕсли вы не пытались войти в аккаунт — проигнорируйте это сообщение.");
        mailMessage.setText(text.toString());

        try {
            javaMailSender.send(mailMessage);
            return Boolean.TRUE;
        } catch (MailException exception) {
            return Boolean.FALSE;
        }
    }

    @Override
    @Async
    public void sendAlert(Alert alert) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(alert.getEmail());
        mailMessage.setSubject("Опасность");
        mailMessage.setText("Замечена опасная деятельность по IP: "
                + alert.getDangerowIP());
        javaMailSender.send(mailMessage);
    }

    @Override
    @Async
    public void sendOffer(PromoOffer promoOffer) {
        for (String emailOfUser : promoOffer.getEmail()) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("Что-то интересное");
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(emailOfUser);
            switch (promoOffer.getTypeOfOfferMessage()) {
                case UPGRADE:
                    mailMessage.setText(textForUpgrade());
                    break;
                case BUYING:
                    mailMessage.setText(textForBuying());
                    break;
                case DISCOUNT:
                    mailMessage.setText(textForDiscount());
                    break;
            }
            javaMailSender.send(mailMessage);
        }
    }

    private String textForDiscount() {
        return "У НАС ПОЧТИ КОНЧИЛАСЬ ПАМЯТЬ ДЛЯ КЛИЕТВОЙ \n" +
                "УСПЕЙ ЗАРЕЗЕРВИРОВАТЬ ПОДПИСКОЙ СЕБЕ ПАРУ БАЙТ ПАМЯТИ " +
                "ПО СНИЖЕННОЙ ЦЕНЕ";
    }

    private String textForBuying() {
        return "У ТЕБЯ ПОЧТИ КОНЧИЛАСЬ ПАМЯТЬ ДЛЯ КАРТОЧЕК \n" +
                "УСПЕЙ ЗАРЕЗЕРВИРОВАТЬ ПОДПИСКОЙ СЕБЕ ПАРУ БАЙТ ПАМЯТИ";
    }

    private String textForUpgrade() {
        return "БОЛЬШОЙ ЧЛЕН - БОЛЬШИЕ ЯЙЦА, \n" +
                "БОЛЬШОЙ БОРД - БОЛЬШИЕ ПОДПИСКИ";
    }


}
