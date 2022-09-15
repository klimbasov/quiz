package com.innowise.quiz.domain.utill.mapper;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.domain.entity.Quiz;
import org.mapstruct.*;

@Mapper(
        uses = {QuestionMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QuizMapper extends EntityDtoSimpleDtoMapper<Quiz, QuizDto, SimpleQuizDto> {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    void update(QuizDto dto, @MappingTarget Quiz entity);

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "results", ignore = true)
    })
    Quiz toEntity(QuizDto dto);

    @AfterMapping
    default void setRelations(@MappingTarget Quiz target) {
        target.getQuestions().forEach(question -> question.setQuiz(target));
    }
}
