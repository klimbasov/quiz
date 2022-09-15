package com.innowise.quiz.controller.teamlead;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;

@RestController
@RequestMapping("/leading/teams")
@RequiredArgsConstructor
@Validated
public class LeadTeamController {
    private final TeamService service;
    private final AuthorizeLogicService authorizeLogicService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamDto create(@RequestBody @Valid TeamDto dto) {

        return service.create(dto);
    }

    @PatchMapping(value = "/{id}")
    public TeamDto patch(@PathVariable @Min(1) Long id, @RequestBody @Valid TeamDto dto, Principal principal) {
        authorizeLogicService.authorizeTeamAccess(id, principal.getName(), true);
        return service.update(dto, id);
    }

    @PatchMapping(value = "/{id}/entrance")
    public void join(@PathVariable @Min(1) Long id, Principal principal) {
        service.addLeadToTeam(id, principal.getName());
    }
}
