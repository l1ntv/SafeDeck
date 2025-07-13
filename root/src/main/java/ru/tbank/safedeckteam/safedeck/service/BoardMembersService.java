package ru.tbank.safedeckteam.safedeck.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.util.List;

public interface BoardMembersService {

    List<BoardMemberDTO> getBoardMembers(Long boardId, String email);

    BoardMemberDTO updateBoardMember(Long boardId, Long memberId, List<RoleDTO> roles, String email);

    BoardMemberDTO addBoardMember(Long boardId, AddedBoardMemberDTO dto, String email);

    BoardMemberDTO deleteBoardMember(Long boardId, Long memberId, String email);
}
