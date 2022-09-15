package com.innowise.quiz.domain.dto.full;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {
    @Min(0)
    Long id;
    @NotNull(message = "text is mandatory")
    @NotEmpty(message = "text is mandatory")
    String text;
    @NotNull(message = "isCorrect is mandatory")
    Boolean isCorrect;
}
