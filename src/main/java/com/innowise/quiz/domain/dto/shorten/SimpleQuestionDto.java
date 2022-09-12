package com.innowise.quiz.domain.dto.shorten;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleQuestionDto {
    long id;
    String text;
}
