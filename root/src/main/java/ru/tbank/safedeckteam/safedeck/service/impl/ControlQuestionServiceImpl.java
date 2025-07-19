package ru.tbank.safedeckteam.safedeck.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.service.SecurityService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.tbank.safedeckteam.safedeck.model.*;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;
import ru.tbank.safedeckteam.safedeck.model.exception.BoardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.CardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.repository.*;
import ru.tbank.safedeckteam.safedeck.service.ControlQuestionService;
import ru.tbank.safedeckteam.safedeck.service.SecureLogService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;
import ru.tbank.safedeckteam.safedeck.web.mapper.GivenQuestionMapper;
import ru.tbank.safedeckteam.safedeck.web.mapper.QuestionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ControlQuestionServiceImpl implements ControlQuestionService {
    private final ControlQuestionRepository controlQuestionRepository;
    private final BoardRepository boardRepository;
    private final QuestionMapper questionMapper;
    private final GivenQuestionMapper givenQuestionMapper;
    private final ClientRepository clientRepository;
    private final IPRepository ipRepository;
    private final TrustedUsersIPRepository trustedUsersIPRepository;
    private final SecureLogService secureLogService;
    private final CardRepository cardRepository;
    private final StatusRepository statusRepository;

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
    public GivenQuestionDTO getRandomQuestion(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        List<ControlQuestion> questions = board.getControlQuestions();
        ControlQuestion controlQuestion;
        if (!questions.isEmpty()) {
            int randomIndex = new Random().nextInt(questions.size());
            controlQuestion = questions.get(randomIndex);
        } else {
            throw new  RuntimeException("No questions found.");
        }
        return givenQuestionMapper.toDto(controlQuestion);
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

    @Override
    public boolean checkControlQuestion(GivenAnswerDTO givenAnswerDTO, long questionId, String email, HttpServletRequest httpServletRequest) {
        ControlQuestion question = controlQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found."));
        givenAnswerDTO.setCorrectAnswer(question.getAnswer());
        String url = "http://localhost:8082/check-answer";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(givenAnswerDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = new HttpEntity(json, headers);
        RestTemplate restTemplate = new RestTemplate();
        String res = restTemplate.postForObject(url, entity, String.class);
        JsonNode root;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            root = objectMapper.readTree(res);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse given answer from json");
        }
        boolean ans = root.path("result").asBoolean();

        if (ans) {
            Client client = clientRepository.findOptionalByEmail(email)
                    .orElseThrow(() -> new ClientNotFoundException("Client not found."));

            IP ipAddress = ipRepository.findByIp(httpServletRequest.getRemoteAddr())
                    .orElse(IP.builder()
                            .ip(httpServletRequest.getRemoteAddr())
                            .build());
            ipRepository.save(ipAddress);

            TrustedUserIP trustedUserIP = TrustedUserIP.builder()
                    .user(client)
                    .ip(ipAddress)
                    .build();
            trustedUsersIPRepository.save(trustedUserIP);

            CreatedLogDTO createdLogDTO = CreatedLogDTO.builder()
                    .email(email)
                    .httpServletRequest(httpServletRequest)
                    .status(statusRepository.findByName(AuthStatus.OK.name())
                            .orElseThrow(() -> new RuntimeException("Status not found.")))
                    .viewTime(LocalDateTime.now())
                    .cardId(givenAnswerDTO.getCardId())
                    .build();

            secureLogService.createLog(createdLogDTO);
        }

        return ans;
    }
}
