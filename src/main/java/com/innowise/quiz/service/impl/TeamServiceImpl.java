package com.innowise.quiz.service.impl;

import com.innowise.quiz.domain.dto.full.TeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.domain.util.mapper.ext.TeamMapper;
import com.innowise.quiz.repository.TeamRepository;
import com.innowise.quiz.repository.UserRepository;
import com.innowise.quiz.service.config.ServicePropertySource;
import com.innowise.quiz.service.ext.TeamService;
import com.innowise.quiz.service.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.innowise.quiz.service.util.ThrowableLogicUtils.getOrElseThrow;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private final ServicePropertySource propertySource;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMapper mapper;

    @Override
    public TeamDto create(TeamDto dto) {
        Team rawEntity = mapper.toEntity(dto);
        rawEntity.setUsers(rawEntity.getUsers().stream().map(user -> getOrElseThrow(userRepository.findById(user.getId()))).collect(Collectors.toSet()));
        rawEntity.setLeads(rawEntity.getLeads().stream().map(lead -> getOrElseThrow(userRepository.findById(lead.getId()))).collect(Collectors.toSet()));
        Team entity = teamRepository.save(rawEntity);
        return mapper.toDto(entity);
    }

    @Override
    public TeamDto getById(Long id) {
        return mapper.toDto(getOrElseThrow(teamRepository.findById(id)));
    }

    @Override
    public List<SimpleTeamDto> getByName(String name, Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), "name");
        return teamRepository.findTeamsByNameContainingIgnoreCase(name, pageable).map(mapper::toSimpleDto).getContent();

    }

    @Override
    public void addUserToTeam(long teamId, String username) {
        User user = getOrElseThrow(userRepository.findUsersByUsername(username));
        Team team = getOrElseThrow(teamRepository.findById(teamId));

        team.addUser(user);
    }

    @Override
    public void addLeadToTeam(long teamId, String username) {
        User user = getOrElseThrow(userRepository.findUsersByUsername(username));
        Team team = getOrElseThrow(teamRepository.findById(teamId));

        team.addLead(user);
    }

    @Override
    public TeamDto createWithLead(TeamDto dto, String leadName) {

        return null;
    }

    @Override
    public TeamDto delete(Long id) {
        TeamDto dto = mapper.toDto(getOrElseThrow(teamRepository.findById(id)));
        teamRepository.deleteById(id);
        return dto;
    }

    @Override
    public TeamDto update(TeamDto dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TeamDto> getAll(Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), "name");
        return teamRepository.findTeamsByNameContainingIgnoreCase("", pageable).map(mapper::toDto).getContent();
    }
}
