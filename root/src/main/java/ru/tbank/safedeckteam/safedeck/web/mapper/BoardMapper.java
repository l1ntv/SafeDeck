package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.safedeckteam.safedeck.model.Board;
import ru.tbank.safedeckteam.safedeck.web.dto.BoardDTO;

@Mapper(componentModel = "spring")
public interface BoardMapper extends Mappable<Board, BoardDTO> {

    @Mapping(source = "id", target = "boardId")
    @Mapping(source = "name", target = "boardName")
    BoardDTO toDto(Board board);

    @Mapping(source = "boardId", target = "id")
    @Mapping(source = "boardName", target = "name")
    Board toEntity(BoardDTO dto);
}
