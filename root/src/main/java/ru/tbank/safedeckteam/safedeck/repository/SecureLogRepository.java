package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.IP;
import ru.tbank.safedeckteam.safedeck.model.SecureLog;

import java.util.List;
import java.util.Optional;

public interface SecureLogRepository extends JpaRepository<SecureLog,Long> {

    Optional<SecureLog> findByUserAndIp(Client user, IP ip);

    Optional<List<SecureLog>> findByUserAndIpAndCountryAndDeviceAndBoard(Client user, IP ip, String country, String device, Board board);
}
