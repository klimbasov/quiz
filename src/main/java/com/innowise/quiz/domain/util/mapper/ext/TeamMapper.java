package com.innowise.quiz.domain.util.mapper.ext;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.util.mapper.EntityDtoSimpleDtoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper extends EntityDtoSimpleDtoMapper<Team, TeamDto, SimpleTeamDto> {
}
