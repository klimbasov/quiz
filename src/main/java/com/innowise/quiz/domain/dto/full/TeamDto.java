package com.innowise.quiz.domain.dto.full;

import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {
    long id;
    String name;
    List<SimpleUserDto> users = new ArrayList<>();
    List<SimpleUserDto> leads = new ArrayList<>();
    List<SimpleQuizDto> quizzes = new ArrayList<>();
}
