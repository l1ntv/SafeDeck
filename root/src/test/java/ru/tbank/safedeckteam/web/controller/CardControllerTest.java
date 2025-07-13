package ru.tbank.safedeckteam.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.tbank.safedeckteam.safedeck.model.enums.AccessLevel;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.web.controller.CardController;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBoardCards() {
        // Arrange
        Long boardId = 1L;
        String email = "test@example.com";
        UserCardsDTO mockResponse = UserCardsDTO.builder()
                .accessLevel(AccessLevel.OWNER)
                .accessibleCards(List.of(new CardDTO()))
                .build();

        when(cardService.findBoardCards(boardId, email)).thenReturn(mockResponse);

        // Act
        ResponseEntity<UserCardsDTO> response = cardController.getBoardCards(boardId, () -> email);

        // Assert
        assertNotNull(response);
        assertEquals(ResponseEntity.ok(mockResponse), response);
        verify(cardService, times(1)).findBoardCards(boardId, email);
    }

    @Test
    void testCreateCard() {
        // Arrange
        Long boardId = 1L;
        String email = "test@example.com";
        CreatedCardDTO dto = new CreatedCardDTO("Card1", "Desc", null, null);
        CardDTO expected = new CardDTO();

        when(cardService.create(boardId, email, dto)).thenReturn(expected);

        // Act
        ResponseEntity<CardDTO> response = cardController.create(boardId, () -> email, dto);

        // Assert
        assertNotNull(response);
        assertEquals(ResponseEntity.ok(expected), response);
        verify(cardService, times(1)).create(boardId, email, dto);
    }

    @Test
    void testDeleteCard() {
        // Arrange
        Long boardId = 1L;
        Long cardId = 1L;
        String email = "test@example.com";

        doNothing().when(cardService).delete(boardId, cardId, email);

        // Act
        ResponseEntity<Void> response = cardController.delete(boardId, cardId, () -> email);

        // Assert
        assertNotNull(response);
        assertEquals(ResponseEntity.ok().build(), response);
        verify(cardService, times(1)).delete(boardId, cardId, email);
    }

    @Test
    void testRenameCard() {
        // Arrange
        Long boardId = 1L;
        Long cardId = 1L;
        String email = "test@example.com";
        RenamedCardDTO dto = new RenamedCardDTO("NewName");
        CardDTO expected = new CardDTO();

        when(cardService.rename(email, boardId, cardId, dto)).thenReturn(expected);

        // Act
        ResponseEntity<CardDTO> response = cardController.rename(dto, boardId, cardId, () -> email);

        // Assert
        assertNotNull(response);
        assertEquals(ResponseEntity.ok(expected), response);
        verify(cardService, times(1)).rename(email, boardId, cardId, dto);
    }

    @Test
    void testChangeDescription() {
        // Arrange
        Long boardId = 1L;
        Long cardId = 1L;
        String email = "test@example.com";
        ChangedDescriptionCardDTO dto = new ChangedDescriptionCardDTO("New description");
        CardDTO expected = new CardDTO();

        when(cardService.changeDescription(email, boardId, cardId, dto)).thenReturn(expected);

        // Act
        ResponseEntity<CardDTO> response = cardController.changeDescription(dto, boardId, cardId, () -> email);

        // Assert
        assertNotNull(response);
        assertEquals(ResponseEntity.ok(expected), response);
        verify(cardService, times(1)).changeDescription(email, boardId, cardId, dto);
    }
}