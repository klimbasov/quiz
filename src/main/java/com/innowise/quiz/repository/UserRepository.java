package com.innowise.quiz.repository;

import com.innowise.quiz.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findUsersByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Optional<User> findUsersByUsername(String username);

    void deleteUserByUsername(String username);
}
