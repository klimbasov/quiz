package com.innowise.quiz.domain.dto.full;

import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
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
public class TeamDto {
    long id;
    @NotEmpty()
    @Size(min = 4, max = 20)
    String name;
    List<SimpleUserDto> users;
    List<SimpleUserDto> leads;
    List<SimpleQuizDto> quizzes;
}
