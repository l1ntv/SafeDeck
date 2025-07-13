package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.web.dto.ClientDTO;

@Mapper(componentModel = "spring")
public interface ClientMapper extends Mappable<Client, ClientDTO> {

}
