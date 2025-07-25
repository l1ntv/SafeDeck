package ru.tbank.safedeckteam.safedeck.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.model.*;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;
import ru.tbank.safedeckteam.safedeck.model.exception.BoardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.CardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.repository.*;
import ru.tbank.safedeckteam.safedeck.service.SecureLogService;
import ru.tbank.safedeckteam.safedeck.service.StatusService;
import ru.tbank.safedeckteam.safedeck.web.dto.ClientDataDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.CreatedLogDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.LogDTO;
import ru.tbank.safedeckteam.safedeck.web.mapper.LogMapper;

import java.util.Comparator;
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
    private final IPRepository ipRepository;

    @Override
    public List<LogDTO> getBoardLogs(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
        List<SecureLog> secureLogs = board.getSecureLogs();
        List<LogDTO> logDTOS = logMapper.toDtoList(secureLogs);
        logDTOS.sort(Comparator.comparing(LogDTO::getViewTime).reversed());
        return logDTOS;
    }

    @Override
    public LogDTO getFullLog(long logId) {
        SecureLog secureLog = secureLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        if (!secureLog.getStatus().getName().equals(AuthStatus.OK.name())) {
            return logMapper.toDto(secureLog);
        }
        return null;
    }

    @Override
    public Status createLog(CreatedLogDTO createdLogDTO) {
        Client client = clientRepository.findOptionalByEmail(createdLogDTO.getEmail())
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Card card = cardRepository.findById(createdLogDTO.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        Board board = card.getBoard();
        HttpServletRequest httpServletRequest = createdLogDTO.getHttpServletRequest();

        IP ip;
        if (ipRepository.findByIp(httpServletRequest.getRemoteAddr()).orElse(null) == null) {
            ip = IP.builder()
                    .ip(httpServletRequest.getRemoteAddr())
                    .build();
            ip = ipRepository.save(ip);
        } else {
            ip = ipRepository.findByIp(httpServletRequest.getRemoteAddr())
                    .orElseThrow(() -> new RuntimeException("IP not found."));
        }

        String country = httpServletRequest.getLocale() != null ? httpServletRequest.getLocale().getCountry() : "Unknown";
        String device = httpServletRequest.getHeader("User-Agent") == null ? "Unknown" : httpServletRequest.getHeader("User-Agent");
        String provider = (String) httpServletRequest.getSession().getAttribute("user-provider") == null ? "Unknown" : httpServletRequest.getHeader("user-provider");

        ClientDataDTO clientDataDTO = ClientDataDTO.builder()
                .client(client)
                .board(board)
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
                .status(createdLogDTO.getStatus() == null ? statusService.determineStatus(clientDataDTO) : createdLogDTO.getStatus())
                .country(country)
                .device(device)
                .provider(provider)
                .build();

        secureLogRepository.save(secureLog);
        logMapper.toDto(secureLog);

        return secureLog.getStatus();
    }

    @Override
    public void deleteLog(long logId) {
        SecureLog secureLog = secureLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        secureLogRepository.delete(secureLog);
    }
}
