package ru.tbank.safedeckteam.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.tbank.safedeckteam.safedeck.service.ProfileService;
import ru.tbank.safedeckteam.safedeck.web.controller.ProfileController;
import ru.tbank.safedeckteam.safedeck.web.dto.PublicNameResponseDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.UpdatePublicNameRequestDTO;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private final String testEmail = "test@example.com";
    private final Principal testPrincipal = new UsernamePasswordAuthenticationToken(testEmail, "password");

    // Тесты для getPublicName() — 4 теста

    @Test
    void getPublicName_shouldReturnPublicName_whenClientExists() {
        // Arrange
        String expectedPublicName = "Test User";
        PublicNameResponseDTO mockResponse = new PublicNameResponseDTO(expectedPublicName);
        when(profileService.getCurrentUserPublicName(testEmail)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = profileController.getPublicName(testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedPublicName, ((PublicNameResponseDTO) response.getBody()).getPublicName());
        verify(profileService).getCurrentUserPublicName(testEmail);
    }

    @Test
    void getPublicName_shouldThrowException_whenClientNotFound() {
        // Arrange
        when(profileService.getCurrentUserPublicName(testEmail))
                .thenThrow(new RuntimeException("Client not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> profileController.getPublicName(testPrincipal));

        assertEquals("Client not found", exception.getMessage());
        verify(profileService).getCurrentUserPublicName(testEmail);
    }

    @Test
    void getPublicName_shouldThrowNPE_whenPrincipalNull() {
        assertThrows(NullPointerException.class, () ->
                profileController.getPublicName(null));
    }

    @Test
    void getPublicName_shouldReturnValidDtoStructure() {
        // Arrange
        PublicNameResponseDTO mockResponse = new PublicNameResponseDTO("Test User");
        when(profileService.getCurrentUserPublicName(testEmail)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = profileController.getPublicName(testPrincipal);

        // Assert
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PublicNameResponseDTO);
        assertEquals(1, response.getBody().getClass().getDeclaredFields().length);
        assertEquals("publicName", response.getBody().getClass().getDeclaredFields()[0].getName());
    }

    // Тесты для updatePublicName() — 4 теста

    @Test
    void updatePublicName_shouldUpdateAndReturnNewName_whenRequestIsValid() {
        // Arrange
        String newPublicName = "New Name";
        UpdatePublicNameRequestDTO requestDto = new UpdatePublicNameRequestDTO(newPublicName);
        PublicNameResponseDTO mockResponse = new PublicNameResponseDTO(newPublicName);

        when(profileService.updatePublicName(testEmail, requestDto)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = profileController.updatePublicName(testPrincipal, requestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newPublicName, ((PublicNameResponseDTO) response.getBody()).getPublicName());
        verify(profileService).updatePublicName(testEmail, requestDto);
    }

    @Test
    void updatePublicName_shouldThrowException_whenClientNotFound() {
        // Arrange
        UpdatePublicNameRequestDTO requestDto = new UpdatePublicNameRequestDTO("New Name");
        when(profileService.updatePublicName(testEmail, requestDto))
                .thenThrow(new RuntimeException("Client not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> profileController.updatePublicName(testPrincipal, requestDto));

        assertEquals("Client not found", exception.getMessage());
        verify(profileService).updatePublicName(testEmail, requestDto);
    }

    @Test
    void updatePublicName_shouldAcceptEmptyName() {
        // Arrange
        UpdatePublicNameRequestDTO request = new UpdatePublicNameRequestDTO("");
        PublicNameResponseDTO mockResponse = new PublicNameResponseDTO("");
        when(profileService.updatePublicName(testEmail, request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<PublicNameResponseDTO> response =
                profileController.updatePublicName(testPrincipal, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", response.getBody().getPublicName());
    }

    @Test
    void updatePublicName_shouldThrowNPE_whenPrincipalNull() {
        UpdatePublicNameRequestDTO request = new UpdatePublicNameRequestDTO("Name");
        assertThrows(NullPointerException.class, () ->
                profileController.updatePublicName(null, request));
    }
}