package com.innowise.quiz.service;

import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;

import java.util.List;

public interface UserService extends CrudService<UserDto> {

    UserDto getByStrictName(String name);

    List<SimpleUserDto> getByName(String name, Integer page);

    UserDto updateSelf(UserDto dto, Long id);

    UserDto deleteSelf(String name);
}
