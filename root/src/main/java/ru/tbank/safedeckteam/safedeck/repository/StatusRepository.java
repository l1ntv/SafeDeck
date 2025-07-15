package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.safedeckteam.safedeck.model.Status;

public interface StatusRepository extends JpaRepository<Status, Integer> {
}
