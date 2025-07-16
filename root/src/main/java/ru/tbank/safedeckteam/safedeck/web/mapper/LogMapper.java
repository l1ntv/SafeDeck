package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.tbank.safedeckteam.safedeck.model.IP;
import ru.tbank.safedeckteam.safedeck.model.SecureLog;
import ru.tbank.safedeckteam.safedeck.repository.IPRepository;
import ru.tbank.safedeckteam.safedeck.web.dto.LogDTO;

@Mapper(componentModel = "spring", imports = {ru.tbank.safedeckteam.safedeck.model.Client.class,
        SecureLog.class,
        ru.tbank.safedeckteam.safedeck.model.Card.class,
        ru.tbank.safedeckteam.safedeck.model.IP.class,
        ru.tbank.safedeckteam.safedeck.model.Client.class,
        ru.tbank.safedeckteam.safedeck.repository.IPRepository.class})
public interface LogMapper extends Mappable<SecureLog, LogDTO>, IPMapper{

    @Mapping(source = "id", target = "logId")
    @Mapping(expression = "java(secureLog.getUser().getEmail())", target = "email")
    @Mapping(expression = "java(secureLog.getIp().getIp())", target = "ip")
    @Mapping(source = "viewTime", target = "viewTime")
    //@Mapping(expression = "java(secureLog.getCard().getId())", target = "cardId")
    @Mapping(expression = "java(secureLog.getCard().getName())",target = "cardName")
    @Mapping(source = "status.name", target = "status")
    LogDTO toDto(SecureLog secureLog);

    // TODO: cardId -> card и email -> user (Нудно разобраться как работают сервисы в маппере)
    @Mapping(source = "logId", target = "id")
    @Mapping(source = "ip", target = "ip", qualifiedByName = "toIPEntity")
    @Mapping(source = "viewTime", target = "viewTime")
    @Mapping(source = "status", target = "status")
   SecureLog toEntity(LogDTO dto);
}
