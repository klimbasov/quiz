package com.innowise.quiz.domain.util.mapper;

public interface EntityDtoSimpleDtoMapper<E, D, SD> {
    E toEntity(D dto);

    D toDto(E entity);

    SD toSimpleDto(E entity);
}
