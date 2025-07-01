package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.safedeckteam.safedeck.model.Role;
import ru.tbank.safedeckteam.safedeck.web.dto.RoleDTO;

@Mapper(componentModel = "spring")
public interface RoleMapper extends Mappable<Role, RoleDTO> {

    @Mapping(source = "id", target = "roleId")
    @Mapping(source = "name", target = "roleName")
    RoleDTO toDto(Role role);

    @Mapping(source = "roleId", target = "id")
    @Mapping(source = "roleName", target = "name")
    Role toEntity(RoleDTO dto);

}
