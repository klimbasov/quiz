package com.innowise.quiz.controller.user;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Validated
public class TeamController {

    private final TeamService service;

    @GetMapping(value = "/{id}")
    public TeamDto getById(@PathVariable @Min(1) Long id) {
        return service.getById(id);
    }

    @GetMapping(value = "/")
    public List<SimpleTeamDto> get(
            @RequestParam(required = false, defaultValue = "") String partialName,
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page
    ) {
        return service.getByName(partialName, page);
    }

    @PatchMapping(value = "/{teamId}/entrance")
    public void join(@PathVariable @Min(1) Long teamId, Principal principal) {
        service.addUserToTeam(teamId, principal.getName());
    }
}
