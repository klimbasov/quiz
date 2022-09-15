package com.innowise.quiz.service.impl;

import com.innowise.quiz.config.ServicePropertySource;
import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.domain.utill.mapper.UserMapper;
import com.innowise.quiz.repository.TeamRepository;
import com.innowise.quiz.repository.UserRepository;
import com.innowise.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.innowise.quiz.service.utill.PaginationUtils.createPageable;
import static com.innowise.quiz.service.utill.ThrowableLogicUtils.*;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final String SORTING_PROPERTY_NAME = "username";
    private final ServicePropertySource propertySource;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    @Override
    public UserDto create(@Valid UserDto dto) {
        dto = new UserDto()
                .setUsername(dto.getUsername())
                .setPassword(encoder.encode(dto.getPassword()))
                .setRoles(dto.getRoles())
                .setIsAccountNotLocked(true);
        ifPresentThrow(userRepository.findUsersByUsername(dto.getUsername()));
        User entity = mapper.toEntity(dto);
        entity = userRepository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public UserDto getById(Long id) {
        User entity = getOrElseThrow(userRepository.findById(id));
        return mapper.toDto(entity);
    }

    @Override
    public UserDto delete(Long id) {
        User entity = getOrElseThrow(userRepository.findById(id));
        entity.getResults().forEach(quizResult -> quizResult.getQuiz().getResults().remove(quizResult));
        entity.getLeadingTeams().forEach(team -> team.getLeads().remove(entity));
        entity.getTeams().forEach(team -> team.getUsers().remove(entity));
        userRepository.deleteById(id);
        return mapper.toDto(entity);
    }

    @Override
    public UserDto update(@Valid UserDto dto, Long id) {
        User entity = getOrElseThrow(userRepository.findById(id));
        mapper.update(dto, entity);
        mapRelations(dto, entity);
        return mapper.toDto(entity);
    }

    @Override
    public List<UserDto> getAll(Integer page) {
        Pageable pageable = createPageable(page, propertySource.getPageSize(), SORTING_PROPERTY_NAME);
        Page<User> entityPage = userRepository.findUsersByUsernameContainingIgnoreCase("", pageable);
        throwIfPageDoseNotExist(page, entityPage);
        return entityPage.map(mapper::toDto).getContent();
    }

    @Override
    public UserDto getByStrictName(String name) {
        User entity = getOrElseThrow(userRepository.findUsersByUsername(name));
        return mapper.toDto(entity);
    }

    @Override
    public List<SimpleUserDto> getByName(String name, Integer page) {
        Pageable pageable = createPageable(page, propertySource.getPageSize(), SORTING_PROPERTY_NAME);
        Page<User> entityPage = userRepository.findUsersByUsernameContainingIgnoreCase(name, pageable);
        throwIfPageDoseNotExist(page, entityPage);
        return entityPage.map(mapper::toSimpleDto).getContent();
    }

    @Override
    public UserDto updateSelf(UserDto dto, Long id) {
        UserDto restrictedDto = new UserDto()
                .setUsername(dto.getUsername())
                .setPassword(dto.getPassword());
        return this.update(restrictedDto, id);
    }

    @Override
    public UserDto deleteSelf(String name) {
        User user = getOrElseThrow(userRepository.findUsersByUsername(name));
        user.getResults().forEach(quizResult -> quizResult.getQuiz().getResults().remove(quizResult));
        user.getTeams().forEach(team -> team.getUsers().remove(user));
        user.getLeadingTeams().forEach(team -> team.getLeads().remove(user));
        userRepository.delete(user);
        return mapper.toDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User entity = userRepository.findUsersByUsername(username).orElse(null);
        return nonNull(entity) ? new org.springframework.security.core.userdetails.User(entity.getUsername(), entity.getPassword(), entity.getRoles()) : null;
    }

    private void mapRelations(UserDto dto, User entity) {
        if (nonNull(dto.getLeadingTeams())) {
            Set<Team> leadingTeams = dto.getLeadingTeams().stream()
                    .map(simpleTeamDto -> getOrElseThrow(teamRepository.findById(simpleTeamDto.getId())))
                    .collect(Collectors.toSet());
            entity.setLeadingTeams(leadingTeams);
        }
        if (nonNull(dto.getTeams())) {
            Set<Team> teams = dto.getTeams().stream()
                    .map(simpleTeamDto -> getOrElseThrow(teamRepository.findById(simpleTeamDto.getId())))
                    .collect(Collectors.toSet());
            entity.setTeams(teams);
        }
    }
}
