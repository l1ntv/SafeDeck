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
import java.util.stream.Collectors;

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
        Client client = clientRepository.findOptionalByEmail(email)
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
                    boardClient.getEmail(),
                    roleMapper.toDtoList(boardClient.getRoles()));
            dtos.add(boardMembersDTO);
        });
        return dtos;
    }

    @Override
    public BoardMemberDTO getBoardMember(Long boardId, Long memberId, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));
        if (!board.getOwner().equals(client))
            throw new ConflictResourceException("The client is not the owner of the board.");
        Client member = clientRepository.findById(memberId)
                .orElseThrow(() -> new ClientNotFoundException("Member not found."));
        if (!board.getClients().contains(member))
            throw new ConflictResourceException("The client is not a member of this board.");
        return new BoardMemberDTO(member.getId(),
                member.getPublicName(),
                member.getEmail(),
                roleMapper.toDtoList(member.getRoles()));
    }

    @Override
    public BoardMemberDTO updateBoardMember(Long boardId, Long memberId, List<RoleDTO> roles, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
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

        return new BoardMemberDTO(member.getId(), member.getPublicName(), member.getEmail(), roleMapper.toDtoList(member.getRoles()));
    }

    @Override
    @Transactional
    public BoardMemberDTO addBoardMember(Long boardId, AddedBoardMemberDTO dto, String email) {
        Client owner = clientRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));

        if (!board.getOwner().equals(owner)) {
            throw new ConflictResourceException("The client is not the owner of the board.");
        }

        Client member = clientRepository.findOptionalByEmail(dto.getEmail())
                .orElseThrow(() -> new ClientNotFoundException("Member not found."));
        if (owner.getId().equals(member.getId()))
            throw new ConflictResourceException("The owner of the board cannot invite himself.");

        Client finalMember = member;
        if (board.getClients().stream().anyMatch(c -> c.equals(finalMember))) {
            throw new ConflictResourceException("The client is already in the board.");
        }

        // Добавляем участника в доску
        board.getClients().add(member);
        boardRepository.save(board);

        // Собираем все роли, доступные на этой доске (через карты)
        List<Role> boardRoles = board.getCards().stream()
                .flatMap(card -> card.getRoles().stream())
                .collect(Collectors.toList());

        // Проверяем каждую роль из DTO и добавляем пользователю
        for (RoleDTO roleDTO : dto.getRoles()) {
            Role role = roleRepository.findById(roleDTO.getRoleId())
                    .orElseThrow(() -> new RoleNotFoundException("Role not found."));

            if (!boardRoles.contains(role)) {
                throw new ConflictResourceException("The role is missing from the cards on this board.");
            }

            role.getClients().add(member);
            roleRepository.save(role);
        }

        // Сохраняем изменения пользователя
        clientRepository.save(member);
        List<Role> actuallyRoles = roleRepository.findAllByClients_Id(member.getId());
        return new BoardMemberDTO(member.getId(), member.getPublicName(), member.getEmail(), roleMapper.toDtoList(actuallyRoles));
    }

    @Override
    @Transactional
    public BoardMemberDTO deleteBoardMember(Long boardId, Long memberId, String email) {
        Client client = clientRepository.findOptionalByEmail(email)
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

        return new BoardMemberDTO(member.getId(), member.getPublicName(), member.getEmail(), roleMapper.toDtoList(member.getRoles()));
    }
}
