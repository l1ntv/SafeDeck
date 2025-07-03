package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.ControlQuestionService;
import ru.tbank.safedeckteam.safedeck.web.dto.ChangedAnswerDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.ChangedQuestionDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedQuestionDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.QuestionDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class ControlQuestionController {

    private final ControlQuestionService controlQuestionService;

    @GetMapping("/{boardId}")
    public ResponseEntity<List<QuestionDTO>> getBoardQuestions(@PathVariable Long boardId) {
        return ResponseEntity.ok().body(controlQuestionService.getBoardQuestions(boardId));
    }

    @GetMapping("/{boardId}/ids")
    public ResponseEntity<List<Long>> getBoardQuestionsIds(@PathVariable Long boardId) {
        return ResponseEntity.ok().body(controlQuestionService.getBoardQuestionsIds(boardId));
    }

    @GetMapping("/by-id/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long questionId) {
        return ResponseEntity.ok().body(controlQuestionService.getQuestionById(questionId));
    }

    @PostMapping("{boardId}")
    public ResponseEntity<QuestionDTO> createControlQuestion(@PathVariable Long boardId,
                                                             @RequestBody CreatedQuestionDTO createdQuestionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(controlQuestionService.createQuestion(createdQuestionDTO, boardId));
    }

    @DeleteMapping("/{boardId}/{questionId}/delete")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long boardId, @PathVariable Long questionId) {
        controlQuestionService.deleteQuestion(questionId, boardId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{boardId}/{questionId}/change-question")
    public ResponseEntity<QuestionDTO> changeQuestion(@PathVariable Long boardId, @PathVariable Long questionId,
                                                      @RequestBody ChangedQuestionDTO changedQuestionDTO) {
        return ResponseEntity.ok()
                .body(controlQuestionService.changeQuestion(questionId, boardId, changedQuestionDTO));
    }

    @PatchMapping("/{boardId}/{questionId}/change-answer")
    public ResponseEntity<QuestionDTO> changeAnswer(@PathVariable Long boardId, @PathVariable Long questionId,
                                                      @RequestBody ChangedAnswerDTO changedAnswerDTO) {
        return ResponseEntity.ok()
                .body(controlQuestionService.changeAnswer(questionId, boardId, changedAnswerDTO));
    }


}
