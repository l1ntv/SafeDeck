package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.Color;
import ru.tbank.safedeckteam.safedeck.repository.BoardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.ColorRepository;
import ru.tbank.safedeckteam.safedeck.service.BoardService;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedUserBoardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.RenamedBoardDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.BoardDTO;
import ru.tbank.safedeckteam.safedeck.web.mapper.BoardMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    private final ClientRepository clientRepository;

    private final BoardMapper boardMapper;

    private final ColorRepository colorRepository;

    @Override
    public List<BoardDTO> findUserBoards(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));
        List<Board> boards = client.getBoards();
        return boardMapper.toDtoList(boards);
    }

    @Override
    public BoardDTO createBoard(CreatedUserBoardDTO createdUserBoardDTO, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));

        Color color = colorRepository.save(Color.builder().rgbCode("DEFAULT").build());

        Board board = Board.builder()
                .name(createdUserBoardDTO.getBoardName())
                .owner(client)
                .color(color)
                .build();

        boardRepository.save(board);
        return boardMapper.toDto(board);
    }

    @Override
    public BoardDTO renameBoard(Long boardId, RenamedBoardDTO renamedBoardDTO, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        if (!board.getOwner().equals(client)) {
            throw new RuntimeException("The board does not belong to this user.");
        }
        board.setName(renamedBoardDTO.getNewBoardName());
        boardRepository.save(board);
        return boardMapper.toDto(board);
    }

    @Override
    public void deleteBoard(Long boardId, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));
        if (!board.getOwner().equals(client)) {
            throw new RuntimeException("The board does not belong to this user.");
        }
        boardRepository.delete(board);
    }


}
