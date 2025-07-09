package ru.tbank.safedeckteam.safedeck.web.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.safedeckteam.safedeck.model.SecureLog;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.web.dto.LogDTO;

@Mapper(componentModel = "spring", imports = {ru.tbank.safedeckteam.safedeck.model.Client.class,
        ru.tbank.safedeckteam.safedeck.model.SecureLog.class,
        ru.tbank.safedeckteam.safedeck.model.Card.class})
public interface LogMapper extends Mappable<SecureLog, LogDTO>{

    @Mapping(source = "id", target = "logId")
    @Mapping(expression = "java(secureLog.getUser().getEmail())", target = "email")
    @Mapping(source = "ip", target = "ip")
    @Mapping(source = "viewTime", target = "viewTime")
    @Mapping(source = "java(secureLog.getCard().getId())", target = "cardId")
    @Mapping(source = "status", target = "status")
    LogDTO toDto(SecureLog secureLog);

    // TODO: cardId -> card и email -> user (Нудно разобраться как работают сервисы в маппере)
    @Mapping(source = "logId", target = "id")
    @Mapping(source = "ip", target = "ip")
    @Mapping(source = "viewTime", target = "viewTime")
    @Mapping(source = "status", target = "status")
    SecureLog toEntity(LogDTO dto);
}
