package com.innowise.quiz.domain.util.mapper.ext;

import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.domain.util.mapper.EntityDtoSimpleDtoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityDtoSimpleDtoMapper<User, UserDto, SimpleUserDto> {
}
//1 tcp client
//2 udp client
//socket timeout stressless udp!!!
//multiconnect: several clients but one thread (freezin no more then 2 sec)
