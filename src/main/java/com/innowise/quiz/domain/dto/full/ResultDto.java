package com.innowise.quiz.domain.dto.full;

import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    long id;
    @NotEmpty()
    @Size(min = 4, max = 20)
    String name;
    SimpleUserDto user;
    SimpleQuizDto quiz;
    @Max(100)
    @Min(0)
    float result;
}
