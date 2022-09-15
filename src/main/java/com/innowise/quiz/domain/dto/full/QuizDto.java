package com.innowise.quiz.domain.dto.full;

import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {
    long id;
    @NotEmpty()
    @Size(min = 4, max = 50)
    String name;
    SimpleTeamDto team;
    List<QuestionDto> questions = new ArrayList<>();
}
