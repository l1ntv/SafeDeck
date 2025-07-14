package ru.tbank.safedeckteam.safedeck.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.tbank.safedeckteam.safedeck.model.enums.AccessLevel;
import ru.tbank.safedeckteam.safedeck.service.CardService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private final Principal testPrincipal = new UsernamePasswordAuthenticationToken("user@mail.com", "password");
    private final Long testBoardId = 1L;
    private final Long testCardId = 1L;

    // ==================== GET /cards/{boardId} ====================
    @Test
    void getBoardCards_shouldReturnCardsWithAccessLevel() {
        // Arrange
        CardDTO card = CardDTO.builder().cardId(1L).cardName("Task 1").build();
        UserCardsDTO mockResponse = UserCardsDTO.builder()
                .accessibleCards(List.of(card))
                .accessLevel(AccessLevel.PARTICIPANT)
                .build();

        when(cardService.findBoardCards(anyLong(), anyString())).thenReturn(mockResponse);

        // Act
        ResponseEntity<UserCardsDTO> response = cardController.getBoardCards(testBoardId, testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getAccessibleCards().size());
        assertEquals(AccessLevel.PARTICIPANT, response.getBody().getAccessLevel());
    }

    @Test
    void getBoardCards_shouldReturnEmptyListForNewBoard() {
        when(cardService.findBoardCards(anyLong(), anyString()))
                .thenReturn(UserCardsDTO.builder()
                        .accessibleCards(Collections.emptyList())
                        .accessLevel(AccessLevel.OWNER)
                        .build());

        ResponseEntity<UserCardsDTO> response = cardController.getBoardCards(testBoardId, testPrincipal);

        assertTrue(response.getBody().getAccessibleCards().isEmpty());
        assertEquals(AccessLevel.OWNER, response.getBody().getAccessLevel());
    }

    @Test
    void getBoardCards_shouldVerifyBoardOwnership() {
        cardController.getBoardCards(testBoardId, testPrincipal);
        verify(cardService).findBoardCards(eq(testBoardId), eq("user@mail.com"));
    }

    @Test
    void getBoardCards_shouldThrowNPEWhenPrincipalNull() {
        assertThrows(NullPointerException.class, () ->
                cardController.getBoardCards(testBoardId, null));
    }

    // ==================== POST /cards/{boardId} ====================
    @Test
    void create_shouldReturnCardWithGeneratedId() {
        // Arrange
        CreatedCardDTO request = CreatedCardDTO.builder()
                .cardName("New Card")
                .cardDescription("Description")
                .roles(Collections.emptyList())
                .secureData(Collections.emptyList())
                .build();

        CardDTO mockCard = CardDTO.builder().cardId(1L).cardName("New Card").build();
        when(cardService.create(anyLong(), anyString(), any())).thenReturn(mockCard);

        // Act
        ResponseEntity<CardDTO> response = cardController.create(testBoardId, testPrincipal, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getCardId());
        assertEquals("New Card", response.getBody().getCardName());
    }

    @Test
    void create_shouldPassFullDtoToService() {
        CreatedCardDTO request = CreatedCardDTO.builder()
                .cardName("Valid")
                .cardDescription("Desc")
                .roles(List.of(new RoleDTO()))
                .secureData(List.of(new CredentialPairDTO()))
                .build();

        cardController.create(testBoardId, testPrincipal, request);

        verify(cardService).create(eq(testBoardId), eq("user@mail.com"),
                argThat(dto ->
                        dto.getCardName().equals("Valid") &&
                                dto.getRoles().size() == 1 &&
                                dto.getSecureData().size() == 1
                ));
    }


    @Test
    void create_shouldThrowNPEWhenPrincipalNull() {
        CreatedCardDTO request = CreatedCardDTO.builder()
                .cardName("Test")
                .cardDescription("Desc")
                .roles(Collections.emptyList())
                .secureData(Collections.emptyList())
                .build();

        assertThrows(NullPointerException.class, () ->
                cardController.create(testBoardId, null, request));
    }

    @Test
    void create_shouldHandleServiceExceptions() {
        CreatedCardDTO request = CreatedCardDTO.builder()
                .cardName("Test")
                .cardDescription("Desc")
                .roles(Collections.emptyList())
                .secureData(Collections.emptyList())
                .build();

        when(cardService.create(anyLong(), anyString(), any()))
                .thenThrow(new RuntimeException("Board not found"));

        assertThrows(RuntimeException.class, () ->
                cardController.create(testBoardId, testPrincipal, request));
    }

    // ==================== DELETE /cards/{boardId}/{cardId} ====================
    @Test
    void delete_shouldReturnOkOnSuccess() {
        ResponseEntity<Void> response = cardController.delete(testBoardId, testCardId, testPrincipal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void delete_shouldVerifyCardOwnership() {
        cardController.delete(testBoardId, testCardId, testPrincipal);
        verify(cardService).delete(eq(testBoardId), eq(testCardId), eq("user@mail.com"));
    }


    @Test
    void delete_shouldThrowNPEWhenPrincipalNull() {
        assertThrows(NullPointerException.class, () ->
                cardController.delete(testBoardId, testCardId, null));
    }

    @Test
    void delete_shouldPropagateNotFoundExceptions() {
        doThrow(new RuntimeException("Card not found"))
                .when(cardService).delete(anyLong(), anyLong(), anyString());

        assertThrows(RuntimeException.class, () ->
                cardController.delete(testBoardId, 999L, testPrincipal));
    }

    // ==================== PATCH /cards/rename/{boardId}/{cardId} ====================
    @Test
    void rename_shouldReturnUpdatedCard() {
        // Arrange
        RenamedCardDTO request = RenamedCardDTO.builder()
                .newCardName("New Name")
                .build();

        CardDTO mockCard = CardDTO.builder().cardId(1L).cardName("New Name").build();
        when(cardService.rename(anyString(), anyLong(), anyLong(), any()))
                .thenReturn(mockCard);

        // Act
        ResponseEntity<CardDTO> response =
                cardController.rename(request, testBoardId, testCardId, testPrincipal);

        // Assert
        assertEquals("New Name", response.getBody().getCardName());
    }

    @Test
    void rename_shouldThrowNPEWhenPrincipalNull() {
        RenamedCardDTO request = RenamedCardDTO.builder()
                .newCardName("Test")
                .build();

        assertThrows(NullPointerException.class, () ->
                cardController.rename(request, testBoardId, testCardId, null));
    }

    @Test
    void rename_shouldVerifyOwnership() {
        RenamedCardDTO request = RenamedCardDTO.builder()
                .newCardName("Valid")
                .build();

        cardController.rename(request, testBoardId, testCardId, testPrincipal);

        verify(cardService).rename(eq("user@mail.com"), eq(testBoardId), eq(testCardId), any());
    }

    @Test
    void rename_shouldHandleInvalidCardId() {
        RenamedCardDTO request = RenamedCardDTO.builder()
                .newCardName("Test")
                .build();

        when(cardService.rename(anyString(), anyLong(), anyLong(), any()))
                .thenThrow(new RuntimeException("Invalid card ID"));

        assertThrows(RuntimeException.class, () ->
                cardController.rename(request, testBoardId, -1L, testPrincipal));
    }

    // ==================== PATCH /cards/change-description/{boardId}/{cardId} ====================
    @Test
    void changeDescription_shouldReturnUpdatedCard() {
        // Arrange
        ChangedDescriptionCardDTO request = ChangedDescriptionCardDTO.builder()
                .newCardDescription("New Desc")
                .build();

        CardDTO mockCard = CardDTO.builder().cardDescription("New Desc").build();
        when(cardService.changeDescription(anyString(), anyLong(), anyLong(), any()))
                .thenReturn(mockCard);

        // Act
        ResponseEntity<CardDTO> response =
                cardController.changeDescription(request, testBoardId, testCardId, testPrincipal);

        // Assert
        assertEquals("New Desc", response.getBody().getCardDescription());
    }

    @Test
    void changeDescription_shouldAcceptEmptyDescription() {
        ChangedDescriptionCardDTO request = ChangedDescriptionCardDTO.builder()
                .newCardDescription("")
                .build();

        CardDTO mockCard = CardDTO.builder().cardDescription("").build();
        when(cardService.changeDescription(anyString(), anyLong(), anyLong(), any()))
                .thenReturn(mockCard);

        ResponseEntity<CardDTO> response =
                cardController.changeDescription(request, testBoardId, testCardId, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", response.getBody().getCardDescription());
    }

    @Test
    void changeDescription_shouldVerifyOwnership() {
        ChangedDescriptionCardDTO request = ChangedDescriptionCardDTO.builder()
                .newCardDescription("Desc")
                .build();

        cardController.changeDescription(request, testBoardId, testCardId, testPrincipal);

        verify(cardService).changeDescription(eq("user@mail.com"), eq(testBoardId), eq(testCardId), any());
    }

    @Test
    void changeDescription_shouldHandleLongDescriptions() {
        String longDesc = "a".repeat(255);
        ChangedDescriptionCardDTO request = ChangedDescriptionCardDTO.builder()
                .newCardDescription(longDesc)
                .build();

        CardDTO mockCard = CardDTO.builder().cardDescription(longDesc).build();
        when(cardService.changeDescription(anyString(), anyLong(), anyLong(), any()))
                .thenReturn(mockCard);

        ResponseEntity<CardDTO> response =
                cardController.changeDescription(request, testBoardId, testCardId, testPrincipal);

        assertEquals(255, response.getBody().getCardDescription().length());
    }
}