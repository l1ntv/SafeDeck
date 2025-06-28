package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.safedeckteam.safedeck.model.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
