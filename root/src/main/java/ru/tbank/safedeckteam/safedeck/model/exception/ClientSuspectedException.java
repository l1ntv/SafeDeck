package ru.tbank.safedeckteam.safedeck.model.exception;

public class ClientSuspectedException extends RuntimeException {

    public ClientSuspectedException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
      return super.getMessage();
    }
}
