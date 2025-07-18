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
import ru.tbank.safedeckteam.safedeck.service.SecureDataService;
import ru.tbank.safedeckteam.safedeck.web.controller.SecureDataController;
import ru.tbank.safedeckteam.safedeck.web.dto.CredentialPairDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.SecureDataDTO;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecureDataControllerTest {

    @Mock
    private SecureDataService secureDataService;

    @InjectMocks
    private SecureDataController secureDataController;

    @Mock
    private HttpServletRequest httpServletRequest;

    private final Principal testPrincipal = new UsernamePasswordAuthenticationToken("user@mail.com", "password");
    private final Long testCardId = 1L;

    // ==================== GET /secure-data/{cardId} ====================
    @Test
    void getSecureData_shouldReturnDataWithCredentials() {
        // Arrange
        List<CredentialPairDTO> credentials = List.of(
                new CredentialPairDTO("login", "encryptedPass"),
                new CredentialPairDTO("pin", "encryptedPin")
        );
        SecureDataDTO mockResponse = new SecureDataDTO(credentials);
        when(secureDataService.findSecureData(eq(testCardId), eq(testPrincipal.getName())))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<SecureDataDTO> response =
                secureDataController.getSecureData(testCardId, testPrincipal, httpServletRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getCredentials().size());
        assertEquals("login", response.getBody().getCredentials().get(0).getField());
        verify(secureDataService).findSecureData(testCardId, testPrincipal.getName());
    }

    @Test
    void getSecureData_shouldReturnEmptyListWhenNoCredentials() {
        // Arrange
        SecureDataDTO mockResponse = new SecureDataDTO(Collections.emptyList());
        when(secureDataService.findSecureData(anyLong(), anyString()))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<SecureDataDTO> response =
                secureDataController.getSecureData(testCardId, testPrincipal, httpServletRequest);

        // Assert
        assertTrue(response.getBody().getCredentials().isEmpty());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getSecureData_shouldThrowExceptionWhenPrincipalNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                secureDataController.getSecureData(testCardId, null, httpServletRequest));
    }

    @Test
    void getSecureData_shouldHandleServiceExceptions() {
        // Arrange
        when(secureDataService.findSecureData(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                secureDataController.getSecureData(testCardId, testPrincipal, httpServletRequest));
    }

    // ==================== PUT /secure-data/change/{cardId} ====================
    @Test
    void changeSecureData_shouldUpdateAndReturnCredentials() {
        // Arrange
        List<CredentialPairDTO> newCredentials = List.of(
                new CredentialPairDTO("newLogin", "newEncryptedPass")
        );
        SecureDataDTO request = new SecureDataDTO(newCredentials);
        SecureDataDTO mockResponse = new SecureDataDTO(newCredentials);

        when(secureDataService.changeSecureData(eq(testCardId), eq(newCredentials), eq(testPrincipal.getName())))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<SecureDataDTO> response =
                secureDataController.changeSecureData(testCardId, request, testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getCredentials().size());
        assertEquals("newLogin", response.getBody().getCredentials().get(0).getField());
        verify(secureDataService).changeSecureData(testCardId, newCredentials, testPrincipal.getName());
    }

    @Test
    void changeSecureData_shouldHandleEmptyCredentialsList() {
        // Arrange
        SecureDataDTO request = new SecureDataDTO(Collections.emptyList());
        SecureDataDTO mockResponse = new SecureDataDTO(Collections.emptyList());

        when(secureDataService.changeSecureData(anyLong(), anyList(), anyString()))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<SecureDataDTO> response =
                secureDataController.changeSecureData(testCardId, request, testPrincipal);

        // Assert
        assertTrue(response.getBody().getCredentials().isEmpty());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeSecureData_shouldValidatePrincipal() {
        // Arrange
        SecureDataDTO request = new SecureDataDTO(List.of(
                new CredentialPairDTO("field", "value")
        ));

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                secureDataController.changeSecureData(testCardId, request, null));
    }

    @Test
    void changeSecureData_shouldHandleInvalidCredentials() {
        // Arrange
        SecureDataDTO request = new SecureDataDTO(List.of(
                new CredentialPairDTO(null, null) // Invalid data
        ));

        when(secureDataService.changeSecureData(anyLong(), anyList(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                secureDataController.changeSecureData(testCardId, request, testPrincipal));
    }
}