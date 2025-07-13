package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.safedeckteam.safedeck.model.SendSecure;

import java.util.Optional;

@Repository
public interface SendSecureRepository extends JpaRepository<SendSecure, Long> {

    boolean existsByToken(String token);

    Optional<SendSecure> findByToken(String token);
}
