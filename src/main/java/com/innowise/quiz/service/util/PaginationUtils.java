package com.innowise.quiz.service.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
    public static Pageable createPageable(int page, int size, String sortingPropertyName) {
        return PageRequest.of(--page, size, Sort.Direction.ASC, sortingPropertyName);
    }
}
