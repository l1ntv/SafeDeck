package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.CreatedUserBoardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RenamedBoardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.BoardDTO;

import java.util.List;

public interface BoardService {

    List<BoardDTO> findUserBoards(String email);

    BoardDTO createBoard(CreatedUserBoardDTO createdUserBoardDTO, String email);

    BoardDTO renameBoard(Long boardId, RenamedBoardDTO renamedBoardDTO, String email);

    void deleteBoard(Long boardId, String email);
}
