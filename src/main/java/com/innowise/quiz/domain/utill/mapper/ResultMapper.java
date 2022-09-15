package com.innowise.quiz.domain.utill.mapper;

import com.innowise.quiz.domain.dto.full.ResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.domain.entity.Result;
import org.mapstruct.*;

@Mapper(
        uses = {QuizMapper.class},
        componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ResultMapper extends EntityDtoSimpleDtoMapper<Result, ResultDto, SimpleQuizResultDto> {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "quiz", ignore = true),
            @Mapping(target = "user", ignore = true)
    })
    void update(ResultDto source, @MappingTarget Result target);

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "quiz", ignore = true),
            @Mapping(target = "user", ignore = true)
    })
    Result toEntity(ResultDto dto);
}
