package ru.tbank.safedeckteam.safedeckencryptservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.safedeckteam.safedeckencryptservice.model.Secure;

import java.util.Optional;

@Repository
public interface SecureRepository extends JpaRepository<Secure, Long> {

    boolean existsByCardId(Long cardId);

    Optional<Secure> findByCardId(Long cardId);
}
