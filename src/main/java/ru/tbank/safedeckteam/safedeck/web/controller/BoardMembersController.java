package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.BoardMembersService;
import ru.tbank.safedeckteam.safedeck.web.dto.AddedBoardMemberDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.BoardMemberDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.BoardMembersDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RoleDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/board-members")
@RequiredArgsConstructor
public class BoardMembersController {

    private final BoardMembersService boardMembersService;

    @GetMapping("/{boardId}")
    public ResponseEntity<List<BoardMemberDTO>> getBoardMembers(@PathVariable Long boardId,
                                                           Principal principal) {
        return ResponseEntity.ok(boardMembersService.getBoardMembers(boardId, principal.getName()));
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<BoardMemberDTO> addBoardMember(@PathVariable Long boardId,
                                                         @RequestBody AddedBoardMemberDTO dto,
                                                         Principal principal) {
        return ResponseEntity.ok(boardMembersService.addBoardMember(boardId, dto, principal.getName()));
    }

    @PatchMapping("/{boardId}/{memberId}")
    public ResponseEntity<BoardMembersDTO> updateBoardMember(@PathVariable Long boardId,
                                                             @PathVariable Long memberId,
                                                             @RequestBody List<RoleDTO> roles,
                                                             Principal principal) {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{boardId}/{memberId}")
    public ResponseEntity<BoardMemberDTO> deleteBoardMember(@PathVariable Long boardId,
                                                             @PathVariable Long memberId,
                                                             Principal principal) {
        return ResponseEntity.ok(boardMembersService.deleteBoardMember(boardId, memberId, principal.getName()));
    }

}
