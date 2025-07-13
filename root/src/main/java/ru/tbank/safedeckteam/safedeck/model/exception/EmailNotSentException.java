package ru.tbank.safedeckteam.safedeck.model.exception;

public class EmailNotSentException extends RuntimeException {

    public EmailNotSentException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
