package com.innowise.quiz.service.ext;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.service.CrudService;

import java.util.List;

public interface TeamService extends CrudService<TeamDto> {
    List<SimpleTeamDto> getByName(String name, Integer page);

    void addUserToTeam(long teamId, String username);

    void addLeadToTeam(long teamId, String username);

    TeamDto createWithLead(TeamDto dto, String leadName);
}
