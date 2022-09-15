package com.innowise.quiz.domain.utill.mapper;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.domain.entity.Team;
import org.mapstruct.*;

@Mapper(
        uses = {QuizMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TeamMapper extends EntityDtoSimpleDtoMapper<Team, TeamDto, SimpleTeamDto> {
    @Override
    @Mappings({
            @Mapping(target = "quizzes", ignore = true),
            @Mapping(target = "leads", ignore = true),
            @Mapping(target = "users", ignore = true),
            @Mapping(target = "id", ignore = true)
    })
    Team toEntity(TeamDto dto);

    @Mappings({
            @Mapping(target = "quizzes", ignore = true),
            @Mapping(target = "leads", ignore = true),
            @Mapping(target = "users", ignore = true),
            @Mapping(target = "id", ignore = true)
    })
    void update(TeamDto dto, @MappingTarget Team entity);
}
