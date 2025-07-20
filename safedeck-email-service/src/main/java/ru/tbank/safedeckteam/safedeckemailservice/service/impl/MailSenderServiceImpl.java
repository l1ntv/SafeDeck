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
        } catch (MailException exception) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean sendBoardInviteInformation(String email, String boardName, String boardId) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(email);
        mailMessage.setSubject("Вас пригласили в доску в Safedeck.");

        StringBuilder text = new StringBuilder();
        text.append("Здравствуйте!\n")
                .append("Вас пригласили в доску ").append(boardName)
                .append(" в сервисе Safedeck.")
                .append("\nПросим вас зайти в систему и ознакомиться с карточками, к которым у вас есть доступ.")
                .append("\nСсылка на доску: ")
                .append("http://localhost:4200/cards/").append(boardId)
                .append("\nЕсли у вас возникнут вопросы или вам понадобится помощь, мы всегда на связи. ")
                .append("\nС уважением,")
                .append("\nКоманда Safedeck!");
        mailMessage.setText(text.toString());

        try {
            javaMailSender.send(mailMessage);
        } catch (MailException exception) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean sendNewPassword(String email, String publicName, String newPassword) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(email);
        mailMessage.setSubject("Внимание: Подозрение на взлом вашей учетной записи в Safedeck.");

        StringBuilder text = new StringBuilder();
        text.append("Здравствуйте, ").append(publicName).append("!\n\n")
                .append("В вашей учетной записи в сервисе Safedeck были зафиксированы подозрительные действия, ")
                .append("которые могут указывать на несанкционированный доступ.\n\n")
                .append("Для обеспечения безопасности мы изменили пароль вашей учетной записи.\n")
                .append("Новый пароль: ").append(newPassword).append("\n\n")
                .append("Рекомендуем вам:\n")
                .append("- Авторизоваться с этим паролем;\n")
                .append("- Сохранить его в безопасное место;\n")
                .append("- Удалить это письмо из вашей почты, чтобы избежать утечки данных.\n\n")
                .append("Также настоятельно советуем провести полную проверку вашего устройства на наличие вредоносного ПО ")
                .append("с помощью надёжного антивирусного программного обеспечения.\n\n")
                .append("Если у вас возникнут вопросы или проблемы с доступом — пожалуйста, свяжитесь с нами.\n\n")
                .append("С уважением,\n")
                .append("Команда Safedeck");

        mailMessage.setText(text.toString());

        try {
            javaMailSender.send(mailMessage);
        } catch (MailException exception) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
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
