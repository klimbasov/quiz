package com.innowise.quiz.service;

import java.util.List;

public interface CrudService<T> {
    T create(T dto);

    T getById(Long id);

    T delete(Long id);

    T update(T dto);

    List<T> getAll(Integer page);
}
