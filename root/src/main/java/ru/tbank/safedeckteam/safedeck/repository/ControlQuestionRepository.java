package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.safedeckteam.safedeck.model.ControlQuestion;

@Repository
public interface ControlQuestionRepository extends JpaRepository<ControlQuestion, Long> {
}
