package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.ControlQuestion;
import ru.tbank.safedeckteam.safedeck.repository.BoardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ControlQuestionRepository;
import ru.tbank.safedeckteam.safedeck.service.ControlQuestionService;
import ru.tbank.safedeckteam.safedeck.web.dto.ChangedAnswerDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.ChangedQuestionDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedQuestionDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.QuestionDTO;
import ru.tbank.safedeckteam.safedeck.web.mapper.QuestionMapper;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ControlQuestionServiceImpl implements ControlQuestionService {
    private final ControlQuestionRepository controlQuestionRepository;
    private final BoardRepository boardRepository;
    private final QuestionMapper questionMapper;

    @Override
    public QuestionDTO createQuestion(CreatedQuestionDTO createdQuestionDTO, long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        String question = createdQuestionDTO.getQuestion();
        String answer = createdQuestionDTO.getAnswer();
        ControlQuestion controlQuestion = ControlQuestion.builder()
                .question(question)
                .answer(answer)
                .board(board)
                .build();
        controlQuestionRepository.save(controlQuestion);
        return questionMapper.toDto(controlQuestion);
    }

    @Override
    public void deleteQuestion(long questionId, long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        ControlQuestion controlQuestion = controlQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found."));
        if (!board.getControlQuestions().contains(controlQuestion)) {
            throw new RuntimeException("The question does not belong to this board.");
        }
        controlQuestionRepository.delete(controlQuestion);
    }

    @Override
    public List<QuestionDTO> getBoardQuestions(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        List<ControlQuestion> questions = board.getControlQuestions().stream().toList();
        return questionMapper.toDtoList(questions);
    }

    @Override
    public QuestionDTO getQuestionById(long questionId) {
        ControlQuestion question = controlQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found."));
        return questionMapper.toDto(question);
    }

    @Override
    public QuestionDTO changeQuestion(long questionId, long boardId, ChangedQuestionDTO changedQuestionDTO) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        ControlQuestion controlQuestion = controlQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found."));
        if (!board.getControlQuestions().contains(controlQuestion)) {
            throw new RuntimeException("The question does not belong to this board.");
        }
        controlQuestion.setQuestion(changedQuestionDTO.getNewQuestion());
        controlQuestionRepository.save(controlQuestion);
        return questionMapper.toDto(controlQuestion);
    }

    @Override
    public QuestionDTO changeAnswer(long questionId, long boardId, ChangedAnswerDTO changedAnswerDTO) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        ControlQuestion controlQuestion = controlQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found."));
        if (!board.getControlQuestions().contains(controlQuestion)) {
            throw new RuntimeException("The question does not belong to this board.");
        }
        controlQuestion.setAnswer(changedAnswerDTO.getNewAnswer());
        controlQuestionRepository.save(controlQuestion);
        return questionMapper.toDto(controlQuestion);
    }

    @Override
    public List<Long> getBoardQuestionsIds(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        return board.getControlQuestions().stream().map(ControlQuestion::getId).toList();
    }
}
