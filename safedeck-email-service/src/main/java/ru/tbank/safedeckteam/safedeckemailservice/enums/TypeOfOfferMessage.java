package ru.tbank.safedeckteam.safedeckemailservice.enums;

public enum TypeOfOfferMessage {
    UPGRADE("upgrade"),
    BUYING("buying"),
    DISCOUNT("discount");

    private final String type;

    TypeOfOfferMessage(String string) {
        this.type=string;
    }
}
