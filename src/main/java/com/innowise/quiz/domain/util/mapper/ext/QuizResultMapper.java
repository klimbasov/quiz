package com.innowise.quiz.domain.util.mapper.ext;

import com.innowise.quiz.domain.dto.full.QuizResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.domain.entity.QuizResult;
import com.innowise.quiz.domain.util.mapper.EntityDtoSimpleDtoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizResultMapper extends EntityDtoSimpleDtoMapper<QuizResult, QuizResultDto, SimpleQuizResultDto> {
}
