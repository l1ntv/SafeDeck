package ru.tbank.safedeckteam.safedeckemailservice.dto;

import lombok.Getter;
import lombok.Setter;
import ru.tbank.safedeckteam.safedeckemailservice.enums.TypeOfOfferMessage;

import java.util.List;

@Getter
@Setter
public class PromoOffer {
    private List<String> email;
    private TypeOfOfferMessage typeOfOfferMessage;
}
