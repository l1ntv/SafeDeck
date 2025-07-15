package ru.tbank.safedeckteam.safedeck.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.model.*;
import ru.tbank.safedeckteam.safedeck.model.exception.BoardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.CardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.repository.BoardRepository;
import ru.tbank.safedeckteam.safedeck.repository.CardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.SecureLogRepository;
import ru.tbank.safedeckteam.safedeck.service.SecureLogService;
import ru.tbank.safedeckteam.safedeck.service.StatusService;
import ru.tbank.safedeckteam.safedeck.web.dto.ClientDataDTO;
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
    private final StatusService statusService;

    @Override
    public List<LogDTO> getBoardLogs(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
        List<SecureLog> secureLogs = board.getSecureLogs();
        return logMapper.toDtoList(secureLogs);
    }

    @Override
    public void createLog(CreatedLogDTO createdLogDTO) {
        Client client = clientRepository.findOptionalByEmail(createdLogDTO.getEmail())
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Card card = cardRepository.findById(createdLogDTO.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        Board board = card.getBoard();
        HttpServletRequest httpServletRequest = createdLogDTO.getHttpServletRequest();
        IP ip = IP.builder()
                .ip(httpServletRequest.getRemoteAddr())
                .build();

        String country = httpServletRequest.getLocale() != null ? httpServletRequest.getLocale().getCountry() : "Unknown";
        String device = httpServletRequest.getHeader("User-Agent");
        String provider = (String) httpServletRequest.getSession().getAttribute("user-provider");

        ClientDataDTO clientDataDTO = ClientDataDTO.builder()
                .client(client)
                .IP(ip)
                .country(country)
                .device(device)
                .provider(provider)
                .build();

        SecureLog secureLog = SecureLog.builder()
                .board(board)
                .card(card)
                .user(client)
                .ip(ip)
                .viewTime(createdLogDTO.getViewTime())
                .status(statusService.determineStatus(clientDataDTO))
                .country(country)
                .device(device)
                .provider(provider)
                .build();

        secureLogRepository.save(secureLog);
        logMapper.toDto(secureLog);
    }

    @Override
    public void deleteLog(long logId) {
        SecureLog secureLog = secureLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        secureLogRepository.delete(secureLog);
    }
}
