package com.innowise.quiz.domain.utill.mapper;

import com.innowise.quiz.domain.dto.full.QuestionDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuestionDto;
import com.innowise.quiz.domain.entity.Question;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QuestionMapper extends EntityDtoSimpleDtoMapper<Question, QuestionDto, SimpleQuestionDto> {
    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "quiz", ignore = true)
    })
    Question toEntity(QuestionDto dto);

    @AfterMapping
    default void setRelations(@MappingTarget Question target) {
        target.getOptions().forEach(option -> option.setQuestion(target));
    }
}
