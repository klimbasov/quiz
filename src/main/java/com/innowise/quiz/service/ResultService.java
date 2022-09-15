package com.innowise.quiz.service;

import com.innowise.quiz.domain.dto.full.ResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;

import java.util.List;

public interface ResultService extends CrudService<ResultDto> {
    List<SimpleQuizResultDto> getByUsernameAndTeamIdAndByQuizPartialName(
            String username,
            String quizName,
            Integer page);

    List<SimpleQuizResultDto> getByLeadNameAndByQuizId(
            String leadName,
            Long quizId,
            Integer page);

}
