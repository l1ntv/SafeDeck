package ru.tbank.safedeckteam.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.tbank.safedeckteam.safedeck.service.ControlQuestionService;
import ru.tbank.safedeckteam.safedeck.web.controller.ControlQuestionController;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControlQuestionControllerTest {

    @Mock
    private ControlQuestionService controlQuestionService;

    @InjectMocks
    private ControlQuestionController controlQuestionController;

    @Mock
    private HttpServletRequest httpServletRequest;

    private final Principal testPrincipal = new UsernamePasswordAuthenticationToken("user@mail.com", "password");
    private final Long testBoardId = 1L;
    private final Long testQuestionId = 1L;

    // ==================== GET /questions/{boardId} ====================
    @Test
    void getBoardQuestions_shouldReturnQuestionsList() {
        // Arrange
        List<QuestionDTO> mockQuestions = List.of(
                new QuestionDTO(1L, "Question 1", "Answer 1"),
                new QuestionDTO(2L, "Question 2", "Answer 2")
        );
        when(controlQuestionService.getBoardQuestions(testBoardId)).thenReturn(mockQuestions);

        // Act
        ResponseEntity<List<QuestionDTO>> response = controlQuestionController.getBoardQuestions(testBoardId);

        // Assert
        assertEquals(2, response.getBody().size());
        assertEquals("Question 1", response.getBody().get(0).getQuestion());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getBoardQuestions_shouldReturnEmptyListWhenNoQuestions() {
        // Arrange
        when(controlQuestionService.getBoardQuestions(testBoardId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<QuestionDTO>> response = controlQuestionController.getBoardQuestions(testBoardId);

        // Assert
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getBoardQuestions_shouldThrowExceptionWhenServiceFails() {
        // Arrange
        when(controlQuestionService.getBoardQuestions(testBoardId)).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                controlQuestionController.getBoardQuestions(testBoardId));
    }

    @Test
    void getBoardQuestions_shouldReturnCorrectStatus() {
        // Arrange
        when(controlQuestionService.getBoardQuestions(testBoardId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<QuestionDTO>> response = controlQuestionController.getBoardQuestions(testBoardId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ==================== GET /questions/{boardId}/ids ====================
    @Test
    void getBoardQuestionsIds_shouldReturnIdsList() {
        // Arrange
        List<Long> mockIds = List.of(1L, 2L, 3L);
        when(controlQuestionService.getBoardQuestionsIds(testBoardId)).thenReturn(mockIds);

        // Act
        ResponseEntity<List<Long>> response = controlQuestionController.getBoardQuestionsIds(testBoardId);

        // Assert
        assertEquals(3, response.getBody().size());
        assertEquals(1L, response.getBody().get(0));
    }

    @Test
    void getBoardQuestionsIds_shouldReturnEmptyListWhenNoQuestions() {
        // Arrange
        when(controlQuestionService.getBoardQuestionsIds(testBoardId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<Long>> response = controlQuestionController.getBoardQuestionsIds(testBoardId);

        // Assert
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getBoardQuestionsIds_shouldReturnCorrectStatus() {
        // Arrange
        when(controlQuestionService.getBoardQuestionsIds(testBoardId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<Long>> response = controlQuestionController.getBoardQuestionsIds(testBoardId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getBoardQuestionsIds_shouldCallServiceWithCorrectParameters() {
        // Arrange
        when(controlQuestionService.getBoardQuestionsIds(testBoardId)).thenReturn(List.of());

        // Act
        controlQuestionController.getBoardQuestionsIds(testBoardId);

        // Assert
        verify(controlQuestionService).getBoardQuestionsIds(testBoardId);
    }

    // ==================== GET /questions/by-id/{questionId} ====================
    @Test
    void getQuestionById_shouldReturnQuestion() {
        // Arrange
        QuestionDTO mockQuestion = new QuestionDTO(testQuestionId, "Test Question", "Test Answer");
        when(controlQuestionService.getQuestionById(testQuestionId)).thenReturn(mockQuestion);

        // Act
        ResponseEntity<QuestionDTO> response = controlQuestionController.getQuestionById(testQuestionId);

        // Assert
        assertEquals("Test Question", response.getBody().getQuestion());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getQuestionById_shouldReturnCorrectStatus() {
        // Arrange
        when(controlQuestionService.getQuestionById(testQuestionId)).thenReturn(new QuestionDTO());

        // Act
        ResponseEntity<QuestionDTO> response = controlQuestionController.getQuestionById(testQuestionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getQuestionById_shouldCallServiceWithCorrectId() {
        // Arrange
        when(controlQuestionService.getQuestionById(testQuestionId)).thenReturn(new QuestionDTO());

        // Act
        controlQuestionController.getQuestionById(testQuestionId);

        // Assert
        verify(controlQuestionService).getQuestionById(testQuestionId);
    }

    @Test
    void getQuestionById_shouldReturnAllFields() {
        // Arrange
        QuestionDTO mockQuestion = new QuestionDTO(testQuestionId, "Q", "A");
        when(controlQuestionService.getQuestionById(testQuestionId)).thenReturn(mockQuestion);

        // Act
        ResponseEntity<QuestionDTO> response = controlQuestionController.getQuestionById(testQuestionId);

        // Assert
        assertAll(
                () -> assertEquals(testQuestionId, response.getBody().getQuestionId()),
                () -> assertEquals("Q", response.getBody().getQuestion()),
                () -> assertEquals("A", response.getBody().getAnswer())
        );
    }

    // ==================== POST /questions/{boardId} ====================
    @Test
    void createControlQuestion_shouldReturnCreatedStatus() {
        // Arrange
        CreatedQuestionDTO request = new CreatedQuestionDTO("New Q", "New A");
        when(controlQuestionService.createQuestion(request, testBoardId))
                .thenReturn(new QuestionDTO(1L, "New Q", "New A"));

        // Act
        ResponseEntity<QuestionDTO> response =
                controlQuestionController.createControlQuestion(testBoardId, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createControlQuestion_shouldReturnCreatedQuestion() {
        // Arrange
        CreatedQuestionDTO request = new CreatedQuestionDTO("New Q", "New A");
        QuestionDTO mockResponse = new QuestionDTO(1L, "New Q", "New A");
        when(controlQuestionService.createQuestion(request, testBoardId)).thenReturn(mockResponse);

        // Act
        ResponseEntity<QuestionDTO> response =
                controlQuestionController.createControlQuestion(testBoardId, request);

        // Assert
        assertEquals("New Q", response.getBody().getQuestion());
        assertEquals("New A", response.getBody().getAnswer());
    }

    @Test
    void createControlQuestion_shouldCallServiceWithCorrectParameters() {
        // Arrange
        CreatedQuestionDTO request = new CreatedQuestionDTO("Q", "A");
        when(controlQuestionService.createQuestion(request, testBoardId))
                .thenReturn(new QuestionDTO());

        // Act
        controlQuestionController.createControlQuestion(testBoardId, request);

        // Assert
        verify(controlQuestionService).createQuestion(request, testBoardId);
    }



    // ==================== DELETE /questions/{boardId}/{questionId}/delete ====================
    @Test
    void deleteQuestion_shouldReturnOkStatus() {
        // Act
        ResponseEntity<?> response = controlQuestionController.deleteQuestion(testBoardId, testQuestionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteQuestion_shouldCallServiceWithCorrectParameters() {
        // Act
        controlQuestionController.deleteQuestion(testBoardId, testQuestionId);

        // Assert
        verify(controlQuestionService).deleteQuestion(testQuestionId, testBoardId);
    }

    @Test
    void deleteQuestion_shouldNotReturnBody() {
        // Act
        ResponseEntity<?> response = controlQuestionController.deleteQuestion(testBoardId, testQuestionId);

        // Assert
        assertNull(response.getBody());
    }

    @Test
    void deleteQuestion_shouldHandleServiceExceptions() {
        // Arrange
        doThrow(new RuntimeException()).when(controlQuestionService)
                .deleteQuestion(testQuestionId, testBoardId);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                controlQuestionController.deleteQuestion(testBoardId, testQuestionId));
    }

    // ==================== PATCH /questions/{boardId}/{questionId}/change-question ====================
    @Test
    void changeQuestion_shouldReturnUpdatedQuestion() {
        // Arrange
        ChangedQuestionDTO request = new ChangedQuestionDTO("Updated Q");
        QuestionDTO mockResponse = new QuestionDTO(testQuestionId, "Updated Q", "A");
        when(controlQuestionService.changeQuestion(eq(testQuestionId), eq(testBoardId), eq(request)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<QuestionDTO> response =
                controlQuestionController.changeQuestion(testBoardId, testQuestionId, request);

        // Assert
        assertEquals("Updated Q", response.getBody().getQuestion());
    }

    @Test
    void changeQuestion_shouldReturnOkStatus() {
        // Arrange
        ChangedQuestionDTO request = new ChangedQuestionDTO("Q");
        when(controlQuestionService.changeQuestion(eq(testQuestionId), eq(testBoardId), eq(request)))
                .thenReturn(new QuestionDTO());

        // Act
        ResponseEntity<QuestionDTO> response =
                controlQuestionController.changeQuestion(testBoardId, testQuestionId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeQuestion_shouldCallServiceWithCorrectParameters() {
        // Arrange
        ChangedQuestionDTO request = new ChangedQuestionDTO("New Q");
        when(controlQuestionService.changeQuestion(eq(testQuestionId), eq(testBoardId), eq(request)))
                .thenReturn(new QuestionDTO());

        // Act
        controlQuestionController.changeQuestion(testBoardId, testQuestionId, request);

        // Assert
        verify(controlQuestionService).changeQuestion(testQuestionId, testBoardId, request);
    }

    @Test
    void changeQuestion_shouldHandleNullRequest() {
        // Arrange
        doThrow(new NullPointerException()).when(controlQuestionService)
                .changeQuestion(eq(testQuestionId), eq(testBoardId), isNull());

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                controlQuestionController.changeQuestion(testBoardId, testQuestionId, null));
    }

    // ==================== PATCH /questions/{boardId}/{questionId}/change-answer ====================
    @Test
    void changeAnswer_shouldReturnUpdatedQuestion() {
        // Arrange
        ChangedAnswerDTO request = new ChangedAnswerDTO("Updated A");
        QuestionDTO mockResponse = new QuestionDTO(testQuestionId, "Q", "Updated A");
        when(controlQuestionService.changeAnswer(eq(testQuestionId), eq(testBoardId), eq(request)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<QuestionDTO> response =
                controlQuestionController.changeAnswer(testBoardId, testQuestionId, request);

        // Assert
        assertEquals("Updated A", response.getBody().getAnswer());
    }

    @Test
    void changeAnswer_shouldReturnOkStatus() {
        // Arrange
        ChangedAnswerDTO request = new ChangedAnswerDTO("A");
        when(controlQuestionService.changeAnswer(eq(testQuestionId), eq(testBoardId), eq(request)))
                .thenReturn(new QuestionDTO());

        // Act
        ResponseEntity<QuestionDTO> response =
                controlQuestionController.changeAnswer(testBoardId, testQuestionId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeAnswer_shouldCallServiceWithCorrectParameters() {
        // Arrange
        ChangedAnswerDTO request = new ChangedAnswerDTO("New A");
        when(controlQuestionService.changeAnswer(eq(testQuestionId), eq(testBoardId), eq(request)))
                .thenReturn(new QuestionDTO());

        // Act
        controlQuestionController.changeAnswer(testBoardId, testQuestionId, request);

        // Assert
        verify(controlQuestionService).changeAnswer(testQuestionId, testBoardId, request);
    }

    @Test
    void changeAnswer_shouldHandleNullRequest() {
        // Arrange
        doThrow(new NullPointerException()).when(controlQuestionService)
                .changeAnswer(eq(testQuestionId), eq(testBoardId), isNull());

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                controlQuestionController.changeAnswer(testBoardId, testQuestionId, null));
    }

    // ==================== POST /questions/{questionId}/check-question ====================
    @Test
    void checkControlQuestion_shouldReturnValidationResult() {
        // Arrange
        GivenAnswerDTO request = GivenAnswerDTO.builder()
                .correctAnswer("correct")
                .givenAnswer("user answer")
                .boardId(1L)
                .cardId(1L)
                .build();

        when(controlQuestionService.checkControlQuestion(
                eq(request), eq(testQuestionId), eq(testPrincipal.getName()), any()))
                .thenReturn(true);

        // Act
        ResponseEntity<Boolean> response =
                controlQuestionController.checkControlQuestion(testQuestionId, request, testPrincipal, httpServletRequest);

        // Assert
        assertTrue(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void checkControlQuestion_shouldHandleInvalidInput() {
        // Arrange
        GivenAnswerDTO request = GivenAnswerDTO.builder()
                .correctAnswer(null)
                .givenAnswer(null)
                .boardId(0L)
                .cardId(0L)
                .build();

        when(controlQuestionService.checkControlQuestion(
                eq(request), eq(testQuestionId), eq(testPrincipal.getName()), any(HttpServletRequest.class)))
                .thenThrow(new IllegalArgumentException());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                controlQuestionController.checkControlQuestion(testQuestionId, request, testPrincipal, httpServletRequest));
    }

    @Test
    void checkControlQuestion_shouldVerifyServiceParameters() {
        // Arrange
        GivenAnswerDTO request = GivenAnswerDTO.builder()
                .correctAnswer("correct")
                .givenAnswer("user answer")
                .boardId(1L)
                .cardId(1L)
                .build();

        // Act
        controlQuestionController.checkControlQuestion(testQuestionId, request, testPrincipal, httpServletRequest);

        // Assert
        verify(controlQuestionService).checkControlQuestion(
                eq(request),
                eq(testQuestionId),
                eq(testPrincipal.getName()),
                any(HttpServletRequest.class));
    }

    @Test
    void checkControlQuestion_shouldHandlePrincipalNull() {
        // Arrange
        GivenAnswerDTO request = GivenAnswerDTO.builder()
                .correctAnswer("correct")
                .givenAnswer("user answer")
                .boardId(1L)
                .cardId(1L)
                .build();

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                controlQuestionController.checkControlQuestion(testQuestionId, request, null, httpServletRequest));
    }
}