package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.safedeckteam.safedeck.model.SecondFA;

import java.util.Optional;

@Repository
public interface SecondFARepository extends JpaRepository<SecondFA, Long> {

    boolean existsByEmail(String email);

    Optional<SecondFA> findByEmailAndGeneratedCode(String email, String generatedCode);
}
