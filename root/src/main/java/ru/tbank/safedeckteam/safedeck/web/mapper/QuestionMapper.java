package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.safedeckteam.safedeck.model.ControlQuestion;
import ru.tbank.safedeckteam.safedeck.web.dto.QuestionDTO;

@Mapper(componentModel = "spring")
public interface QuestionMapper extends Mappable<ControlQuestion, QuestionDTO>{
    @Mapping(source = "id", target = "questionId")
    @Mapping(source = "question", target = "question")
    @Mapping(source = "answer", target = "answer")
    QuestionDTO toDto(ControlQuestion controlQuestion);

    @Mapping(source = "questionId", target = "id")
    @Mapping(source = "question", target = "question")
    @Mapping(source = "answer", target = "answer")
    ControlQuestion toEntity(QuestionDTO questionDTO);
}
