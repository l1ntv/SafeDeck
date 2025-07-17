package ru.tbank.safedeckteam.safedeck.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.safedeckteam.safedeck.model.ControlQuestion;
import ru.tbank.safedeckteam.safedeck.web.dto.GivenQuestionDTO;

@Mapper(componentModel = "spring")
public interface GivenQuestionMapper extends Mappable<ControlQuestion, GivenQuestionDTO>{

    @Mapping(target = "questionId", source = "id")
    @Mapping(target = "question", source = "question")
    GivenQuestionDTO toDto(ControlQuestion controlQuestion);

    @Mapping(target = "id", source = "questionId")
    @Mapping(target = "question", source = "question")
    ControlQuestion toEntity(GivenQuestionDTO givenQuestionDTO);
}
