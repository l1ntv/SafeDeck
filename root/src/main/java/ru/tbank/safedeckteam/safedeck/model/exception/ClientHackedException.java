package ru.tbank.safedeckteam.safedeck.model.exception;

public class ClientHackedException extends RuntimeException {

    public ClientHackedException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
