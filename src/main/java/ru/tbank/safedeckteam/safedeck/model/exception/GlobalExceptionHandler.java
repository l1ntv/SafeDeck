package ru.tbank.safedeckteam.safedeck.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tbank.safedeckteam.safedeck.web.dto.ErrorResponseDTO;

import java.net.ConnectException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleClientNotFoundException(ClientNotFoundException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBoardNotFoundException(BoardNotFoundException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardNotFoundException(CardNotFoundException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ColorNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleColorNotFoundException(ColorNotFoundException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(GeneratedCodeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneratedCodeNotFoundException(GeneratedCodeNotFoundException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SendSecureNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleSendSecureNotFoundException(SendSecureNotFoundException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConflictResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictResourceException(ConflictResourceException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(WrongDataException.class)
    public ResponseEntity<ErrorResponseDTO> handleWrongDataException(WrongDataException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponseDTO> handleConnectionException(ConnectException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponseDTO("The encryption service is not working."));
    }

    @ExceptionHandler(EmailNotSentException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailNotSentException(EmailNotSentException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponseDTO("The email has not been sent."));
    }
}
