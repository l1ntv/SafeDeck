package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Named;
import ru.tbank.safedeckteam.safedeck.model.IP;

public interface IPMapper {

    @Named("toIPEntity")
    default IP map(String ip) {
        return IP.builder().ip(ip).build();
    }
}
