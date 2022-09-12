package com.innowise.quiz.controller.controller.teamlead;

import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.service.ext.QuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/leading/quizResults")
@RequiredArgsConstructor
public class LeadQuizResultController {
    private final QuizResultService service;

    @GetMapping("/{id}")
    List<SimpleQuizResultDto> getByQuizId(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
            Principal principal
    ) {
        return service.getByLeadNameAndByQuizId(
                principal.getName(),
                id,
                page
        );
    }

    @GetMapping("/")
    List<SimpleQuizResultDto> get(
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
            Principal principal
    ) {
        return service.getByLeadNameAndByQuizId(
                principal.getName(),
                null,
                page
        );
    }

}
