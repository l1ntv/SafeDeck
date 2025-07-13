package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.safedeckteam.safedeck.model.SendSecure;
import ru.tbank.safedeckteam.safedeck.web.dto.SendSecureDTO;

@Mapper(componentModel = "spring")
public interface SendSecureMapper extends Mappable<SendSecure, SendSecureDTO> {

    @Mapping(source = "token", target = "token")
    SendSecureDTO toDto(SendSecure entity);

    @Mapping(source = "token", target = "token")
    SendSecure toEntity(SendSecureDTO entity);
}
