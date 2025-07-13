package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.safedeckteam.safedeck.model.IP;

@Repository
public interface IPRepository extends JpaRepository<IP, Long> {

}
