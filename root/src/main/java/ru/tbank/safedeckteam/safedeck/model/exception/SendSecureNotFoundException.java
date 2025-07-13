package ru.tbank.safedeckteam.safedeck.model.exception;

public class SendSecureNotFoundException extends RuntimeException {
    public SendSecureNotFoundException(String message) {
        super(message);
    }

  @Override
  public String getMessage() {
    return super.getMessage();
  }
}
