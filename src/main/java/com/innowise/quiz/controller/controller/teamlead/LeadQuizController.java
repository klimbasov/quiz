package com.innowise.quiz.controller.controller.teamlead;

import com.innowise.quiz.domain.dto.full.QuizDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.ext.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/leading/quizzes")
@RequiredArgsConstructor
public class LeadQuizController {
    private final QuizService service;
    private final AuthorizeLogicService authorizeLogicService;

    @PostMapping(value = "/")
    QuizDto create(@RequestBody QuizDto dto, Principal principal) {
        authorizeLogicService.authorizeTeamAccess(dto.getTeam().getId(), principal.getName(), true);
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    QuizDto patch(@PathVariable Long id, @RequestBody QuizDto dto, Principal principal) {
        authorizeLogicService.authorizeQuizAccess(id, principal.getName(), true);
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    QuizDto delete(@PathVariable Long id, Principal principal) {
        authorizeLogicService.authorizeQuizAccess(id, principal.getName(), true);
        return service.delete(id);
    }
}
