package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.safedeckteam.safedeck.model.IP;

import java.util.Optional;

@Repository
public interface IPRepository extends JpaRepository<IP, Long> {

    Optional<IP> findByIp(String ip);

    IP findNonOptionalByIp(String ip);

    boolean existsByIp(String ip);
}
