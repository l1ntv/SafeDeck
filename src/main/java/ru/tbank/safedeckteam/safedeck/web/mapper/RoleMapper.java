package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Role;
import ru.tbank.safedeckteam.safedeck.web.dto.BoardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RoleDTO;

@Mapper(componentModel = "spring")
public interface RoleMapper extends Mappable<Role, RoleDTO> {

    @Mapping(source = "id", target = "roleId")
    @Mapping(source = "name", target = "roleName")
    BoardDTO toDto(Board board);

    @Mapping(source = "roleId", target = "id")
    @Mapping(source = "roleName", target = "name")
    Board toEntity(BoardDTO dto);

}
