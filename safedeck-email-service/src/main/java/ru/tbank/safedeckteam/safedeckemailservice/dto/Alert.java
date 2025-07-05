package ru.tbank.safedeckteam.safedeckemailservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Alert {
    private String email;
    private String dangerowIP;
}
