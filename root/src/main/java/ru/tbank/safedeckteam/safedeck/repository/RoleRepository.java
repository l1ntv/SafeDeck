package ru.tbank.safedeckteam.safedeck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.safedeckteam.safedeck.model.Role;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByBoardId(Long boardId);


    @Query("SELECT r FROM Role r JOIN r.clients c WHERE c.id = :clientId AND r.boardId = :boardId")
    List<Role> findRolesByClientIdAndBoardId(@Param("clientId") Long clientId, @Param("boardId") Long boardId);

    List<Role> findAllByClients_Id(Long clientsId);
}
