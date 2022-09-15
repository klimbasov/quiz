package com.innowise.quiz.domain.dto.full;

import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    long id;
    @NotEmpty()
    @Size(min = 4, max = 20)
    String username;
    @NotEmpty()
    @Size(min = 4, max = 20)
    String password;
    List<SimpleTeamDto> teams;
    List<SimpleTeamDto> leadingTeams;
    List<SimpleQuizResultDto> results;
    List<String> roles;
    Boolean isAccountNotLocked;
}
