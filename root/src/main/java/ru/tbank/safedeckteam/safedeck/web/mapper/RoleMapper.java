package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.tbank.safedeckteam.safedeck.model.Role;
import ru.tbank.safedeckteam.safedeck.web.dto.RoleDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper extends Mappable<Role, RoleDTO> {

    @Mapping(source = "id", target = "roleId")
    @Mapping(source = "name", target = "roleName")
    RoleDTO toDto(Role role);

    @Mapping(source = "roleId", target = "id")
    @Mapping(source = "roleName", target = "name")
    Role toEntity(RoleDTO dto);

    @Mapping(source = "id", target = "roleId")
    @Mapping(source = "name", target = "roleName")
    @Named("toRoleDTOList")
    List<RoleDTO> toDtoList(List<Role> roles);

    @Mapping(source = "roleId", target = "id")
    @Mapping(source = "roleName", target = "name")
    @Named("toRoleEntityList")
    List<Role> toEntityList(List<RoleDTO> roles);
}
