package ru.tbank.safedeckteam.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.tbank.safedeckteam.safedeck.service.AuthenticationService;
import ru.tbank.safedeckteam.safedeck.web.controller.AuthController;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private HttpServletRequest httpServletRequest;

    // ==================== POST /auth/generate-register-code ====================
    @Test
    void generateRegisterCode_shouldReturnOkWhenValidRequest() {
        // Arrange
        GenerateRegisterCodeRequestDTO request = new GenerateRegisterCodeRequestDTO();
        request.setEmail("test@example.com");

        // Act
        ResponseEntity<Void> response = authController.generateRegisterCode(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService).generateRegisterCode(request);
    }

    @Test
    void generateRegisterCode_shouldHandleNullEmail() {
        // Arrange
        GenerateRegisterCodeRequestDTO request = new GenerateRegisterCodeRequestDTO();
        request.setEmail(null);

        // Act & Assert
        assertDoesNotThrow(() -> authController.generateRegisterCode(request));
        verify(authenticationService).generateRegisterCode(request);
    }

    @Test
    void generateRegisterCode_shouldHandleServiceException() {
        // Arrange
        GenerateRegisterCodeRequestDTO request = new GenerateRegisterCodeRequestDTO();
        request.setEmail("test@example.com");
        doThrow(new RuntimeException("Service error")).when(authenticationService).generateRegisterCode(any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.generateRegisterCode(request));
    }

    @Test
    void generateRegisterCode_shouldVerifyServiceCall() {
        // Arrange
        GenerateRegisterCodeRequestDTO request = new GenerateRegisterCodeRequestDTO();
        request.setEmail("test@example.com");

        // Act
        authController.generateRegisterCode(request);

        // Assert
        verify(authenticationService).generateRegisterCode(request);
    }

    // ==================== POST /auth/generate-2fa-code ====================
    @Test
    void generate2FACode_shouldReturnOkWhenValidRequest() {
        // Arrange
        Generate2FACodeRequestDTO request = new Generate2FACodeRequestDTO();
        request.setEmail("test@example.com");

        // Act
        ResponseEntity<Void> response = authController.generate2FACode(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService).generate2FACode(request);
    }

    @Test
    void generate2FACode_shouldHandleEmptyEmail() {
        // Arrange
        Generate2FACodeRequestDTO request = new Generate2FACodeRequestDTO();
        request.setEmail("");

        // Act & Assert
        assertDoesNotThrow(() -> authController.generate2FACode(request));
        verify(authenticationService).generate2FACode(request);
    }

    @Test
    void generate2FACode_shouldHandleServiceException() {
        // Arrange
        Generate2FACodeRequestDTO request = new Generate2FACodeRequestDTO();
        request.setEmail("test@example.com");
        doThrow(new RuntimeException("Service error")).when(authenticationService).generate2FACode(any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.generate2FACode(request));
    }

    @Test
    void generate2FACode_shouldVerifyServiceParameters() {
        // Arrange
        Generate2FACodeRequestDTO request = new Generate2FACodeRequestDTO();
        request.setEmail("test@example.com");

        // Act
        authController.generate2FACode(request);

        // Assert
        verify(authenticationService).generate2FACode(request);
    }

    // ==================== POST /auth/register ====================
    @Test
    void register_shouldReturnCreatedStatus() {
        // Arrange
        RegistrationClientRequestDTO request = RegistrationClientRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .publicName("Test User")
                .generatedCode("12345")
                .build();

        RegistrationResponseDTO mockResponse = RegistrationResponseDTO.builder()
                .token("test-token")
                .build();

        when(authenticationService.register(eq(request), any(HttpServletRequest.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<RegistrationResponseDTO> response =
                authController.register(request, httpServletRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("test-token", response.getBody().getToken());
    }

    @Test
    void register_shouldHandleInvalidInput() {
        // Arrange
        RegistrationClientRequestDTO request = RegistrationClientRequestDTO.builder()
                .email(null)
                .password(null)
                .publicName(null)
                .generatedCode(null)
                .build();

        when(authenticationService.register(any(), any()))
                .thenThrow(new IllegalArgumentException());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                authController.register(request, httpServletRequest));
    }

    @Test
    void register_shouldVerifyServiceCall() {
        // Arrange
        RegistrationClientRequestDTO request = RegistrationClientRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .publicName("Test User")
                .generatedCode("12345")
                .build();

        // Act
        authController.register(request, httpServletRequest);

        // Assert
        verify(authenticationService).register(request, httpServletRequest);
    }

    @Test
    void register_shouldHandleServiceException() {
        // Arrange
        RegistrationClientRequestDTO request = RegistrationClientRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .publicName("Test User")
                .generatedCode("12345")
                .build();

        when(authenticationService.register(any(), any()))
                .thenThrow(new RuntimeException("Registration failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                authController.register(request, httpServletRequest));
    }

    // ==================== POST /auth/login ====================
    @Test
    void authenticate_shouldReturnTokenWhenValidCredentials() {
        // Arrange
        AuthenticationRequestDTO request = AuthenticationRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .generatedCode("12345")
                .build();

        AuthenticationResponseDTO mockResponse = AuthenticationResponseDTO.builder()
                .token("test-token")
                .build();

        when(authenticationService.authenticate(request))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<AuthenticationResponseDTO> response =
                authController.authenticate(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test-token", response.getBody().getToken());
    }

    @Test
    void authenticate_shouldHandleInvalidCredentials() {
        // Arrange
        AuthenticationRequestDTO request = AuthenticationRequestDTO.builder()
                .email("test@example.com")
                .password("wrong")
                .generatedCode("12345")
                .build();

        when(authenticationService.authenticate(request))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                authController.authenticate(request));

        assertEquals("Invalid credentials", exception.getMessage());
    }


    @Test
    void authenticate_shouldVerifyServiceCall() {
        // Arrange
        AuthenticationRequestDTO request = AuthenticationRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .generatedCode("12345")
                .build();

        // Act
        authController.authenticate(request);

        // Assert
        verify(authenticationService).authenticate(request);
    }

    @Test
    void authenticate_shouldHandleMissing2FACode() {
        // Arrange
        AuthenticationRequestDTO request = AuthenticationRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .generatedCode(null)
                .build();

        when(authenticationService.authenticate(request))
                .thenThrow(new RuntimeException("2FA code required"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                authController.authenticate(request));

        assertEquals("2FA code required", exception.getMessage());
    }
}