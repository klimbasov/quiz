package com.innowise.quiz.service.utill;

import com.innowise.quiz.domain.entity.Team;
import com.innowise.quiz.domain.entity.User;
import com.innowise.quiz.service.exception.ServiceException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static com.innowise.quiz.service.utill.SelfAccessLogicUtils.isSelfUpdateAcceptable;

public class ThrowableLogicUtils {
    public static void throwIfPageDoseNotExist(Integer page, Page<?> entityPage) {
        if (page > entityPage.getTotalPages()) {
            throw new ServiceException("not found", HttpStatus.NOT_FOUND);
        }
    }

    public static <T> T getOrElseThrow(Optional<T> optional) {
        return optional.orElseThrow(() -> new ServiceException("not found", HttpStatus.NOT_FOUND));
    }

    public static <T> void ifPresentThrow(Optional<T> optional) {
        if (optional.isPresent()) {
            throw new ServiceException("such object already exist", HttpStatus.BAD_REQUEST);
        }
    }

    public static void throwIfSelfAccessDenied(User expected, User actual) {
        if (!isSelfUpdateAcceptable(expected, actual)) {
            throw new ServiceException(new AccessDeniedException("access denied"), HttpStatus.FORBIDDEN);
        }
    }

    public static void throwIfIsNotUserOfTeam(Team team, User user) {
        team
                .getUsers()
                .stream()
                .filter(user1 -> user1.getId().equals(user.getId()))
                .findAny()
                .orElseThrow(() -> {
                    throw new ServiceException(new AccessDeniedException("access denied"), HttpStatus.FORBIDDEN);
                });

    }

    public static void throwIfIsNotLeadOfTeam(Team team, User user) {
        team
                .getLeads()
                .stream()
                .filter(user1 -> user1.getId().equals(user.getId()))
                .findAny()
                .orElseThrow(() -> {
                    throw new ServiceException(new AccessDeniedException("access denied"), HttpStatus.FORBIDDEN);
                });

    }

    public static void throwBadRequestIfConstraintsViolated(DataIntegrityViolationException e) {
        if (e.getCause().getClass().equals(ConstraintViolationException.class)) {
            ServiceException wrapperException = new ServiceException(HttpStatus.BAD_REQUEST);
            wrapperException.initCause(e);
            throw wrapperException;
        } else {
            throw e;
        }
    }
}
