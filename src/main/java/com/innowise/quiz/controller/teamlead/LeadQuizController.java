package com.innowise.quiz.controller.teamlead;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;

@RestController
@RequestMapping("/leading/quizzes")
@RequiredArgsConstructor
@Validated
public class LeadQuizController {
    private final QuizService service;
    private final AuthorizeLogicService authorizeLogicService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizDto create(@RequestBody @Valid QuizDto dto, Principal principal) {
        authorizeLogicService.authorizeTeamAccess(dto.getTeam().getId(), principal.getName(), true);
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public QuizDto patch(@PathVariable @Min(1) Long id, @RequestBody @Valid QuizDto dto, Principal principal) {
        authorizeLogicService.authorizeQuizAccess(id, principal.getName(), true);
        return service.update(dto, id);
    }

    @DeleteMapping("/{id}")
    QuizDto delete(@PathVariable @Min(1) Long id, Principal principal) {
        authorizeLogicService.authorizeQuizAccess(id, principal.getName(), true);
        return service.delete(id);
    }
}
