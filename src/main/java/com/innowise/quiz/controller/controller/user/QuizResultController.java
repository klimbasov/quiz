package com.innowise.quiz.controller.controller.user;

import com.innowise.quiz.domain.dto.full.QuizResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.ext.QuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/quizResults")
@RequiredArgsConstructor
public class QuizResultController {
    private final AuthorizeLogicService authorizeLogicService;
    private final QuizResultService service;

    @GetMapping(value = "/{id}")
    QuizResultDto getById(@PathVariable Long id, Principal principal) {
        authorizeLogicService.authorizeQuizResultAccess(id, principal.getName());
        return service.getById(id);
    }

    @GetMapping(value = "/")
    List<SimpleQuizResultDto> get(
            @RequestParam(required = false, defaultValue = "") String partialName,
            Principal principal,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page
    ) {
        return service.getByUsernameAndTeamIdAndByQuizPartialName(
                principal.getName(),
                partialName,
                page);
    }

    @PostMapping(value = "/")
    QuizResultDto create(@RequestBody QuizResultDto dto, Principal principal) {
        authorizeLogicService.authorizeTeamAccess(dto.getTeam().getId(), principal.getName(), false);
        return service.create(dto);
    }
}
