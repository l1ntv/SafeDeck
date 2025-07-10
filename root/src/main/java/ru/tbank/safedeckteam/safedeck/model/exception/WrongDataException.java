package ru.tbank.safedeckteam.safedeck.model.exception;

public class WrongDataException extends RuntimeException {

    public WrongDataException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
