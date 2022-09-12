package com.innowise.quiz.service.util;

import com.innowise.quiz.domain.entity.User;

import static java.util.Objects.nonNull;

public class SelfAccessLogicUtils {
    public static boolean isSelfUpdateAcceptable(User dto, User existingDto) {
        return (nonNull(dto) || nonNull(existingDto))
                && (dto.getId().equals(existingDto.getId())
                && dto.getRoles().equals(existingDto.getRoles()));
    }
}
