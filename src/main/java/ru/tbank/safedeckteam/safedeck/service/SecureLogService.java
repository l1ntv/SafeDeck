package ru.tbank.safedeckteam.safedeck.service;

import ru.tbank.safedeckteam.safedeck.web.dto.CreatedLogDTO;
import ru.tbank.safedeckteam.safedeck.web.dto.LogDTO;

import java.util.List;

public interface SecureLogService {

    List<LogDTO> getBoardLogs(long boardId);

    void createLog(CreatedLogDTO createdLogDTO);

    void deleteLog(long logId);

}
