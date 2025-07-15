package ru.tbank.safedeckteam.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.service.BoardService;
import ru.tbank.safedeckteam.safedeck.web.controller.BoardController;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    @Mock
    private BoardService boardService;

    private final String testEmail = "test@example.com";
    private final Principal testPrincipal = new UsernamePasswordAuthenticationToken(testEmail, "password");
    private final Long testBoardId = 1L;

    @InjectMocks
    private BoardController boardController;

    // ===============================================
    // Тесты для GET /boards
    // ===============================================

    @Test
    void getUserBoards_shouldReturnBoardList_whenUserBoardExits() {
        //Arrange
        BoardDTO mockBoardDTO = new BoardDTO();
        when(boardService.findUserBoards(testEmail))
                .thenReturn(Collections.singletonList(mockBoardDTO));

        //Act
        ResponseEntity<List<BoardDTO>> response = boardController.getUserBoards(testPrincipal);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockBoardDTO, response.getBody().get(0));
        verify(boardService).findUserBoards(testEmail);
    }

    @Test
    void getUserBoards_shouldReturnEmptyList_whenNoUserBoard() {
        //Arrange
        when(boardService.findUserBoards(testEmail))
                .thenReturn(Collections.emptyList());

        //Act
        ResponseEntity<List<BoardDTO>> response = boardController.getUserBoards(testPrincipal);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(boardService).findUserBoards(testEmail);
    }

    @Test
    void getUserBoards_shouldThrowException_whenUnauthorized() {
        //Act & Assert
        assertThrows(NullPointerException.class,
                () -> boardController.getUserBoards(null) );

        verifyNoInteractions(boardService);
    }

    @Test
    void getUserBoards_shouldValidateInputParameters() {
        //Arrange
        when(boardService.findUserBoards(anyString()))
                .thenReturn(Collections.emptyList());

        //Act
        ResponseEntity<List<BoardDTO>> response = boardController.getUserBoards(testPrincipal);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(boardService).findUserBoards(eq(testEmail));
    }

    // ===============================================
    // Тесты для POST /boards
    // ===============================================

    @Test
    void createBoard_shouldCreateBoard_whenValidRequest() {
        // Arrange
        CreatedUserBoardDTO requestDto = new CreatedUserBoardDTO();
        requestDto.setBoardName("NewBoard");

        BoardDTO mockBoardDTO = new BoardDTO();
        when(boardService.createBoard(requestDto, testEmail))
                .thenReturn(mockBoardDTO);

        // Act
        ResponseEntity<BoardDTO> response =
                boardController.createBoard(requestDto, testPrincipal);

        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBoardDTO, response.getBody());
        verify(boardService).createBoard(requestDto, testEmail);
    }

    @Test
    void createBoard_shouldRequireAuthentication() {
        // Arrange
        CreatedUserBoardDTO requestDto = new CreatedUserBoardDTO();
        requestDto.setBoardName("NewBoard");

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                boardController.createBoard(requestDto, null));

        verifyNoInteractions(boardService);
    }

    @Test
    void createBoard_shouldHandleServiceExceptions() {
        // Arrange
        CreatedUserBoardDTO requestDto = new CreatedUserBoardDTO();
        requestDto.setBoardName("NewBoard");

        when(boardService.createBoard(requestDto, testEmail))
                .thenThrow(new ClientNotFoundException("Client not found."));

        // Act & Assert
        assertThrows(ClientNotFoundException.class, () ->
                boardController.createBoard(requestDto, testPrincipal));
    }

    // ===============================================
    // Тесты для PATCH /boards/{boardId}/rename
    // ===============================================

//    @Test
//    void renameBoard_shouldReturn() {
//        // Arrange
//        RenamedBoardDTO renamedBoardDTO = new RenamedBoardDTO();
//
//        // Act
//        ResponseEntity<BoardDTO> response =
//                boardController.renameBoard(testBoardId, renamedBoardDTO, testPrincipal);
//
//        // Assert
//        //assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNull(response.getBody());
//        verifyNoInteractions(boardService);
//    }
}
