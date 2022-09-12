package com.innowise.quiz.domain.dto.full;

import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    long id = 0;
    String username = null;
    String password = null;
    //List<SimpleQuizResultDto> quizzes = new ArrayList<>();
    List<SimpleTeamDto> teams = new ArrayList<>();
    List<SimpleTeamDto> leadingTeams = new ArrayList<>();
    List<SimpleQuizResultDto> results = new ArrayList<>();
    List<String> roles = new ArrayList<>();
    Boolean isAccountNotLocked = true;
}
