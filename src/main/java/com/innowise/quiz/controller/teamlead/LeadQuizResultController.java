package com.innowise.quiz.controller.teamlead;

import com.innowise.quiz.domain.dto.shorten.SimpleQuizResultDto;
import com.innowise.quiz.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/leading/results")
@RequiredArgsConstructor
@Validated
public class LeadQuizResultController {
    private final ResultService service;

    @GetMapping("/{id}")
    public List<SimpleQuizResultDto> getByQuizId(
            @PathVariable @Min(1) Long id,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
            Principal principal
    ) {
        return service.getByLeadNameAndByQuizId(principal.getName(), id, page);
    }

    @GetMapping("/")
    public List<SimpleQuizResultDto> get(
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
