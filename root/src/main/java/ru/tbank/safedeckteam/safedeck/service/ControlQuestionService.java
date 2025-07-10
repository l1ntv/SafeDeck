package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.util.List;

public interface ControlQuestionService {

    QuestionDTO createQuestion(CreatedQuestionDTO createdQuestionDTO, long boardId);

    void deleteQuestion(long questionId, long boardId);

    List<QuestionDTO> getBoardQuestions(long boardId);

    QuestionDTO getQuestionById(long questionId);

    QuestionDTO changeQuestion(long questionId, long boardId, ChangedQuestionDTO changedQuestionDTO);

    QuestionDTO changeAnswer(long questionId, long boardId, ChangedAnswerDTO changedAnswerDTO);

    List<Long> getBoardQuestionsIds(long boardId);

    boolean checkControlQuestion(GivenAnswerDTO givenAnswerDTO, long questionId);
}
