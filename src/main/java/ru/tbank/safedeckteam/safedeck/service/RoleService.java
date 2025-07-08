package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.AddedCardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RoleDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RoleWithCardsDTO;

import java.util.List;

public interface RoleService {

    List<RoleDTO> findRoles(Long boardId, String email);

    RoleDTO createRole(Long boardId, String roleName, String email);

    void deleteRole(Long boardId, Long roleId, String email);

    RoleWithCardsDTO updateRole(Long roleId, Long boardId, List<AddedCardDTO> cards, String email);
}
