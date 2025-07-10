package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.Builder;
import lombok.Data;
import ru.tbank.safedeckteam.safedeck.model.enums.AccessLevel;

import java.util.List;

@Data
@Builder
public class UserCardsDTO {

    List<CardDTO> accessibleCards;

    AccessLevel accessLevel;
}
