package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.safedeckteam.safedeck.model.Card;
import ru.tbank.safedeckteam.safedeck.web.dto.CardDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface CardMapper extends Mappable<Card, CardDTO> {

    @Mapping(source = "id", target = "cardId")
    @Mapping(source = "name", target = "cardName")
    @Mapping(source = "description", target = "cardDescription")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "toRoleDTOList")
    CardDTO toDto(Card card);

    @Mapping(source = "cardId", target = "id")
    @Mapping(source = "cardName", target = "name")
    @Mapping(source = "cardDescription", target = "description")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "toRoleEntityList")
    Card toEntity(CardDTO dto);

    @Mapping(source = "id", target = "cardId")
    @Mapping(source = "name", target = "cardName")
    @Mapping(source = "description", target = "cardDescription")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "toRoleDTOList")
    List<CardDTO> toDtoList(List<Card> cards);
}
