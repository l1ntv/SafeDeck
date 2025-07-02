package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.CreatedQuestionDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.QuestionDTO;

import java.util.List;

public interface ControlQuestionService {

    QuestionDTO createQuestion(CreatedQuestionDTO createdQuestionDTO, long boardId);

    void deleteQuestion(long questionId, long boardId);

    List<QuestionDTO> getBoardQuestions(long boardId);

    QuestionDTO changeQuestion(long questionId, long boardId);

    QuestionDTO changeAnswer(long questionId, long boardId);
}
