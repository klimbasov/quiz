package com.innowise.quiz.domain.util.mapper.ext;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.domain.entity.Quiz;
import com.innowise.quiz.domain.util.mapper.EntityDtoSimpleDtoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizMapper extends EntityDtoSimpleDtoMapper<Quiz, QuizDto, SimpleQuizDto> {
}
