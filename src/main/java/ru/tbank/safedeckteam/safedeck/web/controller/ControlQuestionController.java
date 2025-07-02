package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.safedeckteam.safedeck.service.ControlQuestionService;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class ControlQuestionController {
    private final ControlQuestionService controlQuestionService;


}
