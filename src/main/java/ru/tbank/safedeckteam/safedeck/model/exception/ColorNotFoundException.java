package ru.tbank.safedeckteam.safedeck.model.exception;

public class ColorNotFoundException extends RuntimeException {

    public ColorNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
