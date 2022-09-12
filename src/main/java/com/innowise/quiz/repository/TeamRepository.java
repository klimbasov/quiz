package com.innowise.quiz.repository;

import com.innowise.quiz.domain.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Page<Team> findTeamsByNameContainingIgnoreCase(String name, Pageable pageable);
}
