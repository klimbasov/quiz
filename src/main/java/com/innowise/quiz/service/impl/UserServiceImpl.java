package com.innowise.quiz.service.impl;

import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.dto.shorten.SimpleTeamDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.domain.util.mapper.ext.UserMapper;
import com.innowise.quiz.repository.UserRepository;
import com.innowise.quiz.service.config.ServicePropertySource;
import com.innowise.quiz.service.ext.UserService;
import com.innowise.quiz.service.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.innowise.quiz.service.util.ThrowableLogicUtils.*;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final ServicePropertySource propertySource;
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    @Override
    public UserDto create(UserDto dto) {
        dto = dto.toBuilder().password(encoder.encode(dto.getPassword())).build();
        ifPresentThrow(repository.findUsersByUsername(dto.getUsername()));
        User rawEntity = mapper.toEntity(dto);
        User entity = repository.save(rawEntity);
        return mapper.toDto(entity);
    }

    @Override
    public UserDto getById(Long id) {
        User entity = getOrElseThrow(repository.findById(id));
        return mapper.toDto(entity);
    }

    @Override
    public UserDto delete(Long id) {
        User entity = getOrElseThrow(repository.findById(id));
        repository.deleteById(id);
        return mapper.toDto(entity);
    }

    @Override
    public UserDto update(UserDto dto) {
        UserDto oldDto = mapper.toDto(getOrElseThrow(repository.findById(dto.getId())));
        UserDto newDto = merge(oldDto, dto);
        User entity = mapper.toEntity(newDto);
        repository.save(entity);
        return newDto;
    }

    @Override
    public List<UserDto> getAll(Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), "username");
        Page<User> entityPage = repository.findUsersByUsernameContainingIgnoreCase("", pageable);
        throwIfPageDoseNotExist(page, entityPage);
        return entityPage.map(mapper::toDto).getContent();
    }

    @Override
    public UserDto getByStrictName(String name) {
        User entity = getOrElseThrow(repository.findUsersByUsername(name));
        return mapper.toDto(entity);
    }

    @Override
    public List<SimpleUserDto> getByName(String name, Integer page) {
        Pageable pageable = PaginationUtils.createPageable(page, propertySource.getPageSize(), "username");
        Page<User> entityPage = repository.findUsersByUsernameContainingIgnoreCase(name, pageable);
        throwIfPageDoseNotExist(page, entityPage);
        return entityPage.map(mapper::toSimpleDto).getContent();
    }

    @Override
    public UserDto updateSelf(UserDto dto) {
        UserDto oldDto = mapper.toDto(getOrElseThrow(repository.findById(dto.getId())));
        return update(mergeAsUser(oldDto, dto));
    }

    private UserDto mergeAsUser(UserDto oldDto, UserDto dto) {
        String password = isNull(dto.getPassword()) ? oldDto.getPassword() : encoder.encode(dto.getPassword() + propertySource.getSecret());
        String username = isNull(dto.getUsername()) ? oldDto.getUsername() : dto.getUsername();
        return oldDto.toBuilder()
                .password(password)
                .username(username)
                .build();
    }

    private UserDto merge(UserDto oldDto, UserDto dto) {
        String password = isNull(dto.getPassword()) ? oldDto.getPassword() : encoder.encode(dto.getPassword() + propertySource.getSecret());
        String username = isNull(dto.getUsername()) ? oldDto.getUsername() : dto.getUsername();
        List<SimpleTeamDto> teams = isNull(dto.getTeams()) ? oldDto.getTeams() : dto.getTeams();
        List<String> roles = isNull(dto.getRoles()) ? oldDto.getRoles() : dto.getRoles();
        Boolean isAccountLocked = isNull(dto.getIsAccountNotLocked()) ? oldDto.getIsAccountNotLocked() : dto.getIsAccountNotLocked();
        return oldDto.toBuilder()
                .password(password)
                .username(username)
                .roles(roles)
                .teams(teams)
                .isAccountNotLocked(isAccountLocked)
                .build();
    }


    @Override
    public UserDto deleteSelf(String name) {
        User user = getOrElseThrow(repository.findUsersByUsername(name));
        repository.deleteUserByUsername(name);
        return mapper.toDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User entity = repository.findUsersByUsername(username).orElse(null);
        return new org.springframework.security.core.userdetails.User(entity.getUsername(), entity.getPassword(), entity.getRoles());
    }
}
