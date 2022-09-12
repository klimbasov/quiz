package com.innowise.quiz.service.ext;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.service.CrudService;

import java.util.List;

public interface QuizService extends CrudService<QuizDto> {
    List<SimpleQuizDto> getByName(String name, Integer page);
}
