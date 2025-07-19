package ru.tbank.safedeckteam.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;
import ru.tbank.safedeckteam.safedeck.service.SecureLogService;
import ru.tbank.safedeckteam.safedeck.web.controller.SecureLogController;
import ru.tbank.safedeckteam.safedeck.web.dto.LogDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecureLogControllerTest {

    @Mock
    private SecureLogService secureLogService;

    @InjectMocks
    private SecureLogController secureLogController;

    private final Long testBoardId = 1L;
    private final Long testLogId = 1L;
    private final LocalDateTime testTime = LocalDateTime.now();

    // ==================== GET /logs/{boardId} ====================
    @Test
    void getBoardLogs_shouldReturnListOfLogs() {
        // Arrange
        LogDTO log1 = LogDTO.builder()
                .logId(1L)
                .email("user1@example.com")
                .ip("192.168.1.1")
                .viewTime(testTime)
                .cardName("Card 1")
                .status(AuthStatus.OK)
                .build();

        LogDTO log2 = LogDTO.builder()
                .logId(2L)
                .email("user2@example.com")
                .ip("192.168.1.2")
                .viewTime(testTime)
                .cardName("Card 2")
                .status(AuthStatus.SUSPECT)
                .build();

        when(secureLogService.getBoardLogs(testBoardId)).thenReturn(List.of(log1, log2));

        // Act
        ResponseEntity<List<LogDTO>> response = secureLogController.getBoardLogs(testBoardId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("user1@example.com", response.getBody().get(0).getEmail());
        assertEquals(AuthStatus.SUSPECT, response.getBody().get(1).getStatus());
    }

    @Test
    void getBoardLogs_shouldReturnEmptyListWhenNoLogsExist() {
        when(secureLogService.getBoardLogs(testBoardId)).thenReturn(List.of());

        ResponseEntity<List<LogDTO>> response = secureLogController.getBoardLogs(testBoardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getBoardLogs_shouldHandleServiceExceptions() {
        when(secureLogService.getBoardLogs(testBoardId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> secureLogController.getBoardLogs(testBoardId));
    }

    @Test
    void getBoardLogs_shouldCallServiceWithCorrectParameters() {
        secureLogController.getBoardLogs(testBoardId);

        verify(secureLogService).getBoardLogs(testBoardId);
    }

    // ==================== GET /logs/{logId}/all ====================
    @Test
    void getFullLog_shouldReturnLogDetails() {
        LogDTO mockLog = LogDTO.builder()
                .logId(testLogId)
                .email("admin@example.com")
                .ip("10.0.0.1")
                .viewTime(testTime)
                .cardName("Admin Card")
                .status(AuthStatus.HACK)
                .build();

        when(secureLogService.getFullLog(testLogId)).thenReturn(mockLog);

        ResponseEntity<LogDTO> response = secureLogController.getFullLog(testLogId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testLogId, response.getBody().getLogId());
        assertEquals(AuthStatus.HACK, response.getBody().getStatus());
    }

    @Test
    void getFullLog_shouldHandleLogNotFound() {
        when(secureLogService.getFullLog(testLogId)).thenThrow(new RuntimeException("Log not found"));

        assertThrows(RuntimeException.class, () -> secureLogController.getFullLog(testLogId));
    }

    @Test
    void getFullLog_shouldReturnOkStatusForValidLog() {
        when(secureLogService.getFullLog(testLogId)).thenReturn(new LogDTO());

        ResponseEntity<LogDTO> response = secureLogController.getFullLog(testLogId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getFullLog_shouldPassCorrectLogIdToService() {
        secureLogController.getFullLog(testLogId);

        verify(secureLogService).getFullLog(testLogId);
    }

    // ==================== DELETE /logs/{logId}/delete ====================
    @Test
    void deleteLog_shouldReturnOkStatus() {
        ResponseEntity<?> response = secureLogController.deleteLog(testLogId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteLog_shouldCallServiceWithCorrectLogId() {
        secureLogController.deleteLog(testLogId);

        verify(secureLogService).deleteLog(testLogId);
    }

    @Test
    void deleteLog_shouldHandleNonExistentLog() {
        doThrow(new RuntimeException("Log not found")).when(secureLogService).deleteLog(testLogId);

        assertThrows(RuntimeException.class, () -> secureLogController.deleteLog(testLogId));
    }

    @Test
    void deleteLog_shouldVerifyServiceInteraction() {
        secureLogController.deleteLog(testLogId);

        verify(secureLogService, times(1)).deleteLog(testLogId);
    }
}