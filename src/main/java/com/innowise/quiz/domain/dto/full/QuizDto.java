package com.innowise.quiz.domain.dto.full;

import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {
    long id;
    String name;
    SimpleTeamDto team;
    List<QuestionDto> questions = new ArrayList<>();
    SimpleUserDto lead;
}
