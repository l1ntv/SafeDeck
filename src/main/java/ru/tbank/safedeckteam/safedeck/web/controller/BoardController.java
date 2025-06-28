package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.service.BoardService;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedUserBoardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RenamedBoardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.BoardDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping()
    public ResponseEntity<List<BoardDTO>> getUserBoards(Principal principal) {
        return ResponseEntity.ok()
                .body(boardService.findUserBoards(principal.getName()));
    }

    @PostMapping()
    public ResponseEntity<BoardDTO> createBoard(@RequestBody CreatedUserBoardDTO request,
                                                Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.createBoard(request, principal.getName()));
    }

    @PatchMapping("/{boardId}/rename")
    public ResponseEntity<BoardDTO> renameBoard(@PathVariable Long boardId,
                                                @RequestBody RenamedBoardDTO request,
                                                Principal principal) {
        return ResponseEntity.ok()
                .body(boardService.renameBoard(boardId, request, principal.getName()));
    }

    @DeleteMapping("/{boardId}/delete")
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId, Principal principal) {
        boardService.deleteBoard(boardId, principal.getName());
        return ResponseEntity.ok().build();
    }
}
