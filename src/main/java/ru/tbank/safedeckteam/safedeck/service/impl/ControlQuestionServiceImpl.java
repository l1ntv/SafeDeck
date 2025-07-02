package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.repository.ControlQuestionRepository;
import ru.tbank.safedeckteam.safedeck.service.ControlQuestionService;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedQuestionDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.QuestionDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ControlQuestionServiceImpl implements ControlQuestionService {
    private final ControlQuestionRepository controlQuestionRepository;

    @Override
    public QuestionDTO createQuestion(CreatedQuestionDTO createdQuestionDTO, long boardId) {
        return null;
    }

    @Override
    public void deleteQuestion(long questionId, long boardId) {

    }

    @Override
    public List<QuestionDTO> getBoardQuestions(long boardId) {
        return List.of();
    }

    @Override
    public QuestionDTO changeQuestion(long questionId, long boardId) {
        return null;
    }

    @Override
    public QuestionDTO changeAnswer(long questionId, long boardId) {
        return null;
    }
}
