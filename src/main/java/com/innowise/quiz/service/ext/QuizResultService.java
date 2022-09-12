package com.innowise.quiz.service.ext;

import com.innowise.quiz.domain.dto.full.QuizResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.service.CrudService;

import java.util.List;

public interface QuizResultService extends CrudService<QuizResultDto> {
    List<SimpleQuizResultDto> getByUsernameAndTeamIdAndByQuizPartialName(
            String username,
            String quizName,
            Integer page);

    List<SimpleQuizResultDto> getByLeadNameAndByQuizId(
            String leadName,
            Long quizId,
            Integer page);

}
