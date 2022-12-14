package com.innowise.quiz.service.utill;

import com.innowise.quiz.service.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ThrowableLogicUtilsTest {

    @Test
    void throwIfPageDoseNotExist() {
        List<Object> objects = List.of(new Object(), new Object(), new Object());
        Page<?> page = new PageImpl<>(objects);
        assertDoesNotThrow(() -> ThrowableLogicUtils.throwIfPageDoseNotExist(0, page));
        assertThrows(ServiceException.class, () -> ThrowableLogicUtils.throwIfPageDoseNotExist(page.getTotalPages()+1, page));
    }

    @Test
    void getOrElseThrow() {
        Optional<?> empty = Optional.empty();
        Optional<?> notEmpty = Optional.of(new Object());
        Optional<?> nullReference = null;
        assertThrows(ServiceException.class, () -> ThrowableLogicUtils.getOrElseThrow(nullReference));
        assertDoesNotThrow(() -> ThrowableLogicUtils.getOrElseThrow(notEmpty));
        assertThrows(ServiceException.class, () -> ThrowableLogicUtils.getOrElseThrow(empty));
    }
}