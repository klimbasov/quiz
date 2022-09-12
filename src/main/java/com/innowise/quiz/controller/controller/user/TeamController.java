package com.innowise.quiz.controller.controller.user;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.service.AuthorizeLogicService;
import com.innowise.quiz.service.ext.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService service;

    @GetMapping(value = "/{id}")
    TeamDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    List<SimpleTeamDto> get(
            @RequestParam(required = false, defaultValue = "") String partialName,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page
    ) {
        return service.getByName(partialName, page);
    }

    @PatchMapping(value = "/{teamId}/entrance")
    void join(@PathVariable Long teamId, Principal principal) {
        service.addUserToTeam(teamId, principal.getName());
    }
}
