package ru.tbank.safedeckteam.safedeck.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.safedeckteam.safedeck.repository.SecureLogRepository;
import ru.tbank.safedeckteam.safedeck.service.SecureLogService;
import ru.tbank.safedeckteam.safedeck.web.dto.LogDTO;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class SecureLogController {

    private final SecureLogService secureLogService;

    @GetMapping("/{boardId}")
    public ResponseEntity<List<LogDTO>> getBoardLogs(@PathVariable Long boardId) {
        return ResponseEntity.ok().body(secureLogService.getBoardLogs(boardId));
    }

    @GetMapping("/{logId}/all")
    public ResponseEntity<LogDTO> getFullLog(@PathVariable Long logId) {
        return ResponseEntity.ok().body(secureLogService.getFullLog(logId));
    }

    @DeleteMapping("/{logId}/delete")
    public ResponseEntity<?> deleteLog(@PathVariable Long logId) {
        secureLogService.deleteLog(logId);
        return  ResponseEntity.ok().build();
    }
}
