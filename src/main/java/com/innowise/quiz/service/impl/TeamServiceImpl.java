package com.innowise.quiz.service.impl;

import com.innowise.quiz.config.ServicePropertySource;
import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.domain.entity.Quiz;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.domain.utill.mapper.TeamMapper;
import com.innowise.quiz.repository.QuizRepository;
import com.innowise.quiz.repository.TeamRepository;
import com.innowise.quiz.repository.UserRepository;
import com.innowise.quiz.service.TeamService;
import com.innowise.quiz.service.utill.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.innowise.quiz.service.utill.ThrowableLogicUtils.*;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {
    private static final String SORTING_PROPERTY_NAME = "name";
    private final ServicePropertySource propertySource;
    private final TeamRepository teamRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final TeamMapper mapper;

    @Override
    public TeamDto create(TeamDto dto) {
        Team entity = mapper.toEntity(dto);
        mapRelations(dto, entity);
        try {
            entity = teamRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throwBadRequestIfConstraintsViolated(e);
        }
        return mapper.toDto(entity);
    }


    @Override
    public TeamDto getById(Long id) {
        return mapper.toDto(getOrElseThrow(teamRepository.findById(id)));
    }

    @Override
    public List<SimpleTeamDto> getByName(String name, Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), SORTING_PROPERTY_NAME);
        Page<Team> teams = teamRepository.findTeamsByNameContainingIgnoreCase(name, pageable);
        throwIfPageDoseNotExist(page, teams);
        return teams.map(mapper::toSimpleDto).getContent();

    }

    @Override
    public void addUserToTeam(long teamId, String username) {
        User user = getOrElseThrow(userRepository.findUsersByUsername(username));
        Team team = getOrElseThrow(teamRepository.findById(teamId));

        team.getUsers().add(user);
        user.getTeams().add(team);
    }

    @Override
    public void addLeadToTeam(long teamId, String username) {
        User lead = getOrElseThrow(userRepository.findUsersByUsername(username));
        Team team = getOrElseThrow(teamRepository.findById(teamId));

        team.getLeads().add(lead);
        lead.getLeadingTeams().add(team);
    }

    @Override
    public TeamDto delete(Long id) {
        Team entity = getOrElseThrow(teamRepository.findById(id));
        entity.getUsers().forEach(user -> user.getTeams().remove(entity));
        entity.getLeads().forEach(lead -> lead.getLeadingTeams().remove(entity));
        entity.getQuizzes().forEach(quiz -> {
            quiz.getResults()
                    .forEach(result -> result.getUser().getResults().remove(result));
        });
        teamRepository.delete(entity);
        return mapper.toDto(entity);
    }

    @Override
    public TeamDto update(TeamDto dto, Long id) {
        Team entity = getOrElseThrow(teamRepository.findById(id));
        mapper.update(dto, entity);
        mapRelations(dto, entity);
        return mapper.toDto(entity);
    }

    @Override
    public List<TeamDto> getAll(Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), SORTING_PROPERTY_NAME);
        Page<Team> teams = teamRepository.findTeamsByNameContainingIgnoreCase("", pageable);
        throwIfPageDoseNotExist(page, teams);
        return teams.map(mapper::toDto).getContent();
    }

    private void mapRelations(TeamDto dto, Team entity) {
        if (nonNull(dto.getLeads())) {
            Set<User> leads = dto.getLeads().stream()
                    .map(simpleUserDto -> getOrElseThrow(userRepository.findById(simpleUserDto.getId())))
                    .collect(Collectors.toSet());
            entity.setLeads(leads);
            leads.forEach(user -> user.getLeadingTeams().add(entity));
        }
        if (nonNull(dto.getUsers())) {
            Set<User> users = dto.getUsers().stream()
                    .map(simpleUserDto -> getOrElseThrow(userRepository.findById(simpleUserDto.getId())))
                    .collect(Collectors.toSet());
            entity.setUsers(users);
            users.forEach(user -> user.getTeams().add(entity));
        }
        if (nonNull(dto.getQuizzes())) {
            Set<Quiz> quizzes = dto.getQuizzes().stream()
                    .map(simpleQuizDto -> getOrElseThrow(quizRepository.findById(simpleQuizDto.getId())))
                    .collect(Collectors.toSet());
            entity.setQuizzes(quizzes);
            quizzes.forEach(quiz -> quiz.setTeam(entity));
        }
    }
}
