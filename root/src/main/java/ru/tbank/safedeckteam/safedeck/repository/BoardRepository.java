package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.safedeckteam.safedeck.model.Board;

import java.util.Optional;

@Transactional
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.cards WHERE b.id = :id")
    Optional<Board> findWithCardsById(@Param("id") Long id);
}
