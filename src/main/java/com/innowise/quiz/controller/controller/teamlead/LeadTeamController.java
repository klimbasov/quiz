package com.innowise.quiz.controller.controller.teamlead;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.ext.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/leading/teams")
@RequiredArgsConstructor
public class LeadTeamController {
    private final TeamService service;
    private final AuthorizeLogicService authorizeLogicService;

    @PostMapping(value = "/")
    TeamDto create(@RequestBody TeamDto dto) {

        return service.create(dto);
    }

    @PatchMapping(value = "/{id}")
    TeamDto update(@PathVariable Long id, @RequestBody TeamDto dto, Principal principal) {
        authorizeLogicService.authorizeTeamAccess(id, principal.getName(), true);
        return service.update(dto);
    }

    @PatchMapping(value = "/{id}/entrance")
    void join(@PathVariable Long id, Principal principal) {
        service.addLeadToTeam(id, principal.getName());
    }
}
