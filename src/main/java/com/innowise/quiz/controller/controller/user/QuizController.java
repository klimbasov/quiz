package com.innowise.quiz.controller.controller.user;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.ext.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService service;
    private final AuthorizeLogicService authorizeLogicService;

    @GetMapping(value = "/{id}")
    QuizDto getById(@PathVariable Long id, Principal principal) {
        authorizeLogicService.authorizeQuizAccess(id, principal.getName(), false);
        return service.getById(id);
    }

    @GetMapping(value = "/")
    List<SimpleQuizDto> get(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page
    ) {
        return service.getByName(name, page);
    }
}
