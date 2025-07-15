package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.RoleService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{boardId}")
    public ResponseEntity<List<RoleWithCardsDTO>> getRoles(@PathVariable Long boardId,
                                                  Principal principal) {
        return ResponseEntity.ok(roleService.findRoles(boardId, principal.getName()));
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<RoleDTO> createRole(@PathVariable Long boardId,
                                              @RequestBody CreatedRoleDTO dto,
                                              Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.createRole(boardId, dto.getRoleName(), principal.getName()));
    }

    @DeleteMapping("/{boardId}/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long boardId,
                                           @PathVariable Long roleId,
                                           Principal principal) {
        roleService.deleteRole(boardId, roleId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{boardId}/{roleId}")
    public ResponseEntity<RoleWithCardsDTO> addCardsToRole(@PathVariable Long roleId,
                                                           @PathVariable Long boardId,
                                                           @RequestBody List<AddedCardDTO> cards,
                                                           Principal principal) {
        return ResponseEntity.ok(roleService.updateRole(roleId, boardId, cards, principal.getName()));
    }

    @PatchMapping("/{boardId}/rename/{roleId}")
    public ResponseEntity<RoleDTO> renameRole(@PathVariable Long roleId,
                                              @PathVariable Long boardId,
                                              @RequestBody RenamedRoleDTO dto,
                                              Principal principal) {
        return ResponseEntity.ok(roleService.renameRole(roleId, boardId, dto.getNewRoleName(), principal.getName()));
    }
}
