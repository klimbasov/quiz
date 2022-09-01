package com.innowise.quiz.domain.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class QuizResult {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group", nullable = false)
    private Group group;

    @Column(name = "result")
    private Float result;
}
