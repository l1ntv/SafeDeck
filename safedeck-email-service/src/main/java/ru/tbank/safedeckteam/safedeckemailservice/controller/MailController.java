package ru.tbank.safedeckteam.safedeckemailservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeckemailservice.dto.*;
import ru.tbank.safedeckteam.safedeckemailservice.service.MailSenderService;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailSenderService mailSenderService;

    @PostMapping("/send-register-code")
    public ResponseEntity<SendEmailResponseDTO> sendRegisterCode(@RequestBody SendRegisterCodeDTO dto) {
        return ResponseEntity.ok()
                .body(new SendEmailResponseDTO(mailSenderService.sendRegisterCode(dto.getEmail(), dto.getGeneratedCode())));
    }

    @PostMapping("/send-2fa-code")
    public ResponseEntity<SendEmailResponseDTO> send2FACode(@RequestBody Send2FACodeDTO dto) {
        return ResponseEntity.ok()
                .body(new SendEmailResponseDTO(mailSenderService.send2FACode(dto.getEmail(), dto.getGeneratedCode())));
    }

    @PostMapping("/send-board-invite-information")
    public ResponseEntity<SendEmailResponseDTO> sendBoardInviteInformation(@RequestBody SendBoardInviteInformationDTO dto) {
        return ResponseEntity.ok()
                .body(new SendEmailResponseDTO(mailSenderService.sendBoardInviteInformation(dto.getEmail(), dto.getBoardName(), dto.getBoardId())));
    }

    @PostMapping("/alert")
    public ResponseEntity<?> sendAlert(@RequestBody Alert alert) {
        mailSenderService.sendAlert(alert);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sendOffer")
    public ResponseEntity<?> sendAlert(@RequestBody PromoOffer promoOffer) {
        mailSenderService.sendOffer(promoOffer);
        return ResponseEntity.ok().build();
    }
}
