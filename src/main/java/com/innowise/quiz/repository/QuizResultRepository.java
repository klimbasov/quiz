package com.innowise.quiz.repository;

import com.innowise.quiz.domain.entity.QuizResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    @Query("select qr from QuizResult qr where (:username is not null and qr.user.username = :username) and (:name is null or lower(qr.name) like concat('%', lower(:name), '%'))")
    Page<QuizResult> findByName(String username, String name, Pageable pageable);

    @Query("select qr from QuizResult qr where " +
            "((qr.quiz is not null and qr.quiz.lead.username = :leadName)) and" +
            "(:quizId is null or qr.quiz.id = :quizId)")
    Page<QuizResult> findByQuizId(Long quizId, String leadName, Pageable pageable);
}
