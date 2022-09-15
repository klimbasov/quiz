package com.innowise.quiz.repository;

import com.innowise.quiz.domain.entity.Result;
import com.innowise.quiz.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResultRepository extends JpaRepository<Result, Long> {

    @Query("select qr from Result qr where :user = qr.user and qr.quiz.name like concat('%', lower(:quizName), '%')")
    Page<Result> findByUserAndQuizNameContains(User user, String quizName, Pageable pageable);

    @Query("select qr from Result qr where :lead member of qr.quiz.team.leads and(:quizId is null or qr.quiz.id = :quizId)")
    Page<Result> findByQuizId(Long quizId, User lead, Pageable pageable);
}
