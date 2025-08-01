package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Card;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.Role;
import ru.tbank.safedeckteam.safedeck.model.exception.*;
import ru.tbank.safedeckteam.safedeck.repository.BoardRepository;
import ru.tbank.safedeckteam.safedeck.repository.CardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.RoleRepository;
import ru.tbank.safedeckteam.safedeck.service.RoleService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;
import ru.tbank.safedeckteam.safedeck.web.mapper.CardMapper;
import ru.tbank.safedeckteam.safedeck.web.mapper.RoleMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final ClientRepository clientRepository;

    private final BoardRepository boardRepository;

    private final CardRepository cardRepository;

    private final RoleMapper roleMapper;

    private final CardMapper cardMapper;

    @Override
    public List<RoleWithCardsDTO> findRoles(Long boardId, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!client.getId().equals(board.getOwner().getId()))
            throw new ConflictResourceException("The client is not the owner of the board.");
        List<Role> roles = roleRepository.findAllByBoardId(boardId);
        List<RoleWithCardsDTO> roleWithCardsDTOs = new ArrayList<>();
        for (Role role : roles) {
            roleWithCardsDTOs.add(
                    new RoleWithCardsDTO(
                            role.getId(),
                            role.getName(),
                            cardMapper.toDtoList(role.getCards())
                    )
            );
        }
        return roleWithCardsDTOs;
    }

    @Override
    public RoleDTO createRole(Long boardId, String roleName, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!client.getId().equals(board.getOwner().getId()))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Role role = Role.builder()
                .name(roleName)
                .boardId(boardId)
                .build();
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public void deleteRole(Long boardId, Long roleId, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!client.getId().equals(board.getOwner().getId()))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found."));
        if (!boardId.equals(role.getBoardId()))
            throw new ConflictResourceException("The role does not apply to this board.");
        roleRepository.delete(role);
    }

    @Override
    @Transactional
    public RoleWithCardsDTO updateRole(Long roleId, Long boardId, List<AddedCardDTO> cards, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!client.getId().equals(board.getOwner().getId()))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found."));

        if (!boardId.equals(role.getBoardId()))
            throw new ConflictResourceException("The role does not apply to this board.");

        List<Card> cardEntites = new ArrayList<>();
        for (AddedCardDTO dto : cards) {
            Card card = cardRepository.findById(dto.getCardId())
                    .orElseThrow(() -> new CardNotFoundException("Card not found."));
            if (!card.getBoard().getId().equals(boardId))
                throw new ConflictResourceException("The card does not apply to this board.");
            if (!card.getRoles().contains(role))
                card.getRoles().add(role);
            cardEntites.add(card);
        }
        role.setCards(cardEntites);
        roleRepository.save(role);

        Role updatedRole = roleRepository.findById(role.getId())
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));


        return new RoleWithCardsDTO(
                role.getId(),
                role.getName(),
                cardMapper.toDtoList(updatedRole.getCards()));
    }

    @Override
    public RoleDTO renameRole(Long roleId, Long boardId, String newRoleName, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!client.getId().equals(board.getOwner().getId()))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found."));
        if (!boardId.equals(role.getBoardId()))
            throw new ConflictResourceException("The role does not apply to this board.");

        role.setName(newRoleName);
        return roleMapper.toDto(roleRepository.save(role));
    }
}
