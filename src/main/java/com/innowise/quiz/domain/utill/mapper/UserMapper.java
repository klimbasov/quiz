package com.innowise.quiz.domain.utill.mapper;

import com.innowise.quiz.domain.dto.full.UserDto;
import com.innowise.quiz.domain.dto.shorten.SimpleUserDto;
import com.innowise.quiz.domain.entity.User;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {TeamMapper.class, ResultMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper extends EntityDtoSimpleDtoMapper<User, UserDto, SimpleUserDto> {
    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "teams", ignore = true),
            @Mapping(target = "leadingTeams", ignore = true),
            @Mapping(target = "results", ignore = true)
    })
    User toEntity(UserDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "teams", ignore = true),
            @Mapping(target = "leadingTeams", ignore = true),
            @Mapping(target = "results", ignore = true)
    })
    void update(UserDto source, @MappingTarget User target);
}
//1 tcp client
//2 udp client
//socket timeout stressless udp!!!
//multiconnect: several clients but one thread (freezin no more then 2 sec)
