package com.innowise.quiz.repository;

import com.innowise.quiz.domain.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Page<Quiz> findQuizzesByNameContainingIgnoreCase(String name, Pageable pageable);
}
