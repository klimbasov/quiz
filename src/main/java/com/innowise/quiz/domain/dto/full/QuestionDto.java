package com.innowise.quiz.domain.dto.full;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    long id;
    String text;
    List<OptionDto> options = new ArrayList<>();
}
