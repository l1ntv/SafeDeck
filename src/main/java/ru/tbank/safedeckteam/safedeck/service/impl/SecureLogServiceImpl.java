package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Card;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.SecureLog;
import ru.tbank.safedeckteam.safedeck.model.exception.BoardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.CardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.repository.BoardRepository;
import ru.tbank.safedeckteam.safedeck.repository.CardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.SecureLogRepository;
import ru.tbank.safedeckteam.safedeck.service.SecureLogService;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedLogDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.LogDTO;
import ru.tbank.safedeckteam.safedeck.web.mapper.LogMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecureLogServiceImpl implements SecureLogService {

    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;
    private final SecureLogRepository secureLogRepository;
    private final BoardRepository boardRepository;
    private final LogMapper logMapper;

    @Override
    public List<LogDTO> getBoardLogs(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
        //List<SecureLog> secureLogs = board. TODO : сделать связь между доской (Boards) и логами (SecureLog) многие ко многим
        return List.of();
    }

    @Override
    public LogDTO createLog(CreatedLogDTO createdLogDTO) {
        Client client = clientRepository.findByEmail(createdLogDTO.getEmail())
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
        Card card = cardRepository.findById(createdLogDTO.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        Board board = card.getBoard();

        SecureLog secureLog = SecureLog.builder()
                .board(board)
                .card(card)
                .user(client)
                //.ip() // TODO: Добавить получение ip при авторизации
                .viewTime(createdLogDTO.getViewTime())
                //.status() // TODO: Как только смогу определить данные конкретного входа, нужно сравнить с данными из TrustedUserIP + Client
                //.country() // TODO: Добавить получение страны при авторизации
                //.device() // TODO: Добавить получение девайса при авторизации
                //.provider() // TODO: Добавить получение провайдера при авторизации
                .build();

        secureLogRepository.save(secureLog);
        return logMapper.toDto(secureLog);
    }

    @Override
    public void deleteLog(long logId) {
        SecureLog secureLog = secureLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        secureLogRepository.delete(secureLog);
    }
}
