package com.innowise.quiz.controller.user;

import com.innowise.quiz.domain.dto.full.ResultDto;
import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
@Validated
public class ResultController {
    private final AuthorizeLogicService authorizeLogicService;
    private final ResultService service;

    @GetMapping(value = "/{id}")
    public ResultDto getById(@PathVariable @Min(1) Long id, Principal principal) {
        authorizeLogicService.authorizeQuizResultAccess(id, principal.getName());
        return service.getById(id);
    }

    @GetMapping(value = "/")
    public List<SimpleQuizResultDto> get(
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResultDto create(@RequestBody @Valid ResultDto dto, Principal principal) {
        authorizeLogicService.authorizeQuizAccess(dto.getQuiz().getId(), principal.getName(), false);
        return service.create(dto);
    }
}
