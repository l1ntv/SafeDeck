package ru.tbank.safedeckteam.safedeckllm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.safedeckteam.safedeckllm.dto.QuestionCheckRequestDTO;
import ru.tbank.safedeckteam.safedeckllm.dto.ResponseDTO;
import ru.tbank.safedeckteam.safedeckllm.service.LLMService;

@RestController
@RequiredArgsConstructor
public class LLMController {

    private final LLMService llmService;

    @PostMapping("/check-answer")
    ResponseEntity<ResponseDTO> isAnswerCorrect(@RequestBody QuestionCheckRequestDTO request) {
        return ResponseEntity.ok().body(llmService.checkAnswer(request));
    }
}
