package ru.tbank.safedeckteam.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.tbank.safedeckteam.safedeck.service.SendSecureService;
import ru.tbank.safedeckteam.safedeck.web.controller.SendSecureController;
import ru.tbank.safedeckteam.safedeck.web.dto.SendSecureDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.SendSecureDataDTO;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendSecureControllerTest {

    @Mock
    private SendSecureService sendSecureService;

    @InjectMocks
    private SendSecureController sendSecureController;

    private final Principal testPrincipal = new UsernamePasswordAuthenticationToken("user@mail.com", "password");
    private final Long testCardId = 1L;
    private final String testToken = "test-token";

    // ==================== POST /send-secure/{cardId} ====================
    @Test
    void createSendSecureLink_shouldReturnCreatedStatus() {
        // Arrange
        SendSecureDTO mockResponse = new SendSecureDTO("generated-token");
        when(sendSecureService.createSendSecureLink(anyLong(), anyString()))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<SendSecureDTO> response =
                sendSecureController.createSendSecureLink(testCardId, testPrincipal);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("generated-token", response.getBody().getToken());
    }

    @Test
    void createSendSecureLink_shouldThrowNPEWhenPrincipalNull() {
        assertThrows(NullPointerException.class, () ->
                sendSecureController.createSendSecureLink(testCardId, null));
    }

    @Test
    void createSendSecureLink_shouldCallServiceWithCorrectParameters() {
        // Arrange
        SendSecureDTO mockResponse = new SendSecureDTO("token");
        when(sendSecureService.createSendSecureLink(anyLong(), anyString()))
                .thenReturn(mockResponse);

        // Act
        sendSecureController.createSendSecureLink(testCardId, testPrincipal);

        // Assert
        verify(sendSecureService).createSendSecureLink(eq(testCardId), eq("user@mail.com"));
    }

    @Test
    void createSendSecureLink_shouldHandleServiceExceptions() {
        // Arrange
        when(sendSecureService.createSendSecureLink(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Card not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                sendSecureController.createSendSecureLink(testCardId, testPrincipal));
    }

    // ==================== GET /send-secure/{token} ====================
    @Test
    void getSendSecureData_shouldReturnSecureData() {
        // Arrange
        SendSecureDataDTO mockData = new SendSecureDataDTO(
                "Test Card",
                "Description",
                Collections.emptyList()
        );
        when(sendSecureService.getSendSecureData(anyString()))
                .thenReturn(mockData);

        // Act
        ResponseEntity<SendSecureDataDTO> response =
                sendSecureController.getSendSecureData(testToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Card", response.getBody().getCardName());
        assertTrue(response.getBody().getCredentials().isEmpty());
    }

    @Test
    void getSendSecureData_shouldReturnEmptyCredentials() {
        // Arrange
        SendSecureDataDTO mockData = new SendSecureDataDTO(
                "Card",
                "Desc",
                null
        );
        when(sendSecureService.getSendSecureData(anyString()))
                .thenReturn(mockData);

        // Act
        ResponseEntity<SendSecureDataDTO> response =
                sendSecureController.getSendSecureData(testToken);

        // Assert
        assertNull(response.getBody().getCredentials());
    }

    @Test
    void getSendSecureData_shouldCallServiceWithCorrectToken() {
        // Arrange
        when(sendSecureService.getSendSecureData(anyString()))
                .thenReturn(new SendSecureDataDTO());

        // Act
        sendSecureController.getSendSecureData("custom-token");

        // Assert
        verify(sendSecureService).getSendSecureData(eq("custom-token"));
    }

    @Test
    void getSendSecureData_shouldHandleInvalidToken() {
        // Arrange
        when(sendSecureService.getSendSecureData(anyString()))
                .thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                sendSecureController.getSendSecureData("invalid-token"));
    }
}
