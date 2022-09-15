package com.innowise.quiz.domain.dto.shorten;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleTeamDto {
    @Min(1)
    long id;
    String name;
}
