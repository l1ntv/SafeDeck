package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.model.Card;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.Role;
import ru.tbank.safedeckteam.safedeck.model.exception.BoardNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ClientNotFoundException;
import ru.tbank.safedeckteam.safedeck.model.exception.ConflictResourceException;
import ru.tbank.safedeckteam.safedeck.model.exception.RoleNotFoundException;
import ru.tbank.safedeckteam.safedeck.repository.BoardRepository;
import ru.tbank.safedeckteam.safedeck.repository.ClientRepository;
import ru.tbank.safedeckteam.safedeck.repository.RoleRepository;
import ru.tbank.safedeckteam.safedeck.service.BoardMembersService;
import ru.tbank.safedeckteam.safedeck.web.dto.*;
import ru.tbank.safedeckteam.safedeck.web.mapper.ClientMapper;
import ru.tbank.safedeckteam.safedeck.web.mapper.RoleMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardMembersServiceImpl implements BoardMembersService {

    private final ClientRepository clientRepository;

    private final BoardRepository boardRepository;

    private final RoleRepository roleRepository;

    private final ClientMapper clientMapper;

    private final RoleMapper roleMapper;

    @Override
    public List<BoardMemberDTO> getBoardMembers(Long boardId, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().equals(client))
            throw new ConflictResourceException("The client is not the owner of the board.");
        List<Client> boardClients = board.getClients();
        List<BoardMemberDTO> dtos = new ArrayList<>();
        boardClients.forEach(boardClient -> {
            BoardMemberDTO boardMembersDTO = new BoardMemberDTO(boardClient.getId(),
                    boardClient.getPublicName(),
                    roleMapper.toDtoList(boardClient.getRoles()));
            dtos.add(boardMembersDTO);
        });
        return dtos;
    }

    @Override
    public BoardMemberDTO updateBoardMember(Long boardId, Long memberId, List<RoleDTO> roles, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().equals(client))
            throw new ConflictResourceException("The client is not the owner of the board.");

        List<Role> boardRoles = new ArrayList<>();
        for (Card card : board.getCards()) {
            boardRoles.addAll(card.getRoles());
        }

        Client member = clientRepository.findById(memberId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        if (!board.getClients().contains(member))
            throw new ConflictResourceException("The client is not a member of the board.");

        for (RoleDTO dto : roles) {
            Role role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RoleNotFoundException("Role not found."));
            if (!boardRoles.contains(role))
                throw new ConflictResourceException("The role is missing from the cards on this board.");
            member.getRoles().add(role);
        }

        return new BoardMemberDTO(member.getId(), member.getPublicName(), roleMapper.toDtoList(member.getRoles()));
    }

    @Override
    @Transactional
    public BoardMemberDTO addBoardMember(Long boardId, AddedBoardMemberDTO dto, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().equals(client))
            throw new ConflictResourceException("The client is not the owner of the board.");

        Client member = clientRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ClientNotFoundException("Member not found."));
        if (board.getClients().contains(member))
            throw new ConflictResourceException("The client is already in the board.");
        board.getClients().add(member);
        boardRepository.save(board);

        List<Role> boardRoles = new ArrayList<>();
        for (Card card : board.getCards()) {
            boardRoles.addAll(card.getRoles());
        }
        for (RoleDTO roleDTO : dto.getRoles()) {
            Role role = roleRepository.findById(roleDTO.getRoleId())
                    .orElseThrow(() -> new RoleNotFoundException("Role not found."));
            if (!boardRoles.contains(role))
                throw new ConflictResourceException("The role is missing from the cards on this board.");
            member.getRoles().add(role);
        }
        clientRepository.save(member);

        return new BoardMemberDTO(member.getId(), member.getPublicName(), roleMapper.toDtoList(member.getRoles()));
    }

    @Override
    @Transactional
    public BoardMemberDTO deleteBoardMember(Long boardId, Long memberId, String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().equals(client))
            throw new ConflictResourceException("The client is not the owner of the board.");

        Client member = clientRepository.findById(memberId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        List<Client> boardClients = board.getClients();
        if (!boardClients.contains(member))
            throw new ConflictResourceException("The client is not the member of the board.");
        board.getClients().remove(member);
        boardRepository.save(board);

        member.getRoles().removeIf(role -> role.getBoardId().equals(boardId));
        clientRepository.save(client);

        return new BoardMemberDTO(member.getId(), member.getPublicName(), roleMapper.toDtoList(member.getRoles()));
    }
}
