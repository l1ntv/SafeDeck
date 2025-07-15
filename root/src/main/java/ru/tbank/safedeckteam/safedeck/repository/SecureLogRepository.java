package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.safedeckteam.safedeck.model.SecureLog;

public interface SecureLogRepository extends JpaRepository<SecureLog,Long> {
}
