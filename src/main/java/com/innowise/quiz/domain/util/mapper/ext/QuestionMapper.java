package com.innowise.quiz.domain.util.mapper.ext;

import com.innowise.quiz.domain.dto.full.QuestionDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuestionDto;
import com.innowise.quiz.domain.entity.Question;
import com.innowise.quiz.domain.util.mapper.EntityDtoSimpleDtoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper extends EntityDtoSimpleDtoMapper<Question, QuestionDto, SimpleQuestionDto> {
    //@Override
    //@Mapping(target = "options", expression = "java()")
    //Question toEntity(QuestionDto dto);
}
