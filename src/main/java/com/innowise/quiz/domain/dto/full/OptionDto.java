package com.innowise.quiz.domain.dto.full;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {
    long id;
    String text;
    Boolean isCorrect;
}
