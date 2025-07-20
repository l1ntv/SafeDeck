package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.IP;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDataDTO {
    private Client client;

    private Board board;

    private IP IP;

    private String country;

    private String provider;

    private String device;
}
